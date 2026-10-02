package com.example.exchangerate;

import com.example.exchangerate.client.ExchangeRateApiClient;
import com.example.exchangerate.dto.ExchangeRateApiResponse;
import com.example.exchangerate.dto.RatesResponse;
import com.example.exchangerate.repository.ExchangeRateHistoryRepository;
import com.example.exchangerate.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateApiClient apiClient;

    @Mock
    private ExchangeRateHistoryRepository historyRepository;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                exchangeRateService,
                "majorCurrenciesConfig",
                "USD,EUR,JPY,GBP,KRW"
        );
    }

    @Test
    @DisplayName("환율 조회 - 주요 통화만 필터링되어 반환된다")
    void getRates_shouldReturnFilteredMajorCurrencies() {
        // given
        Map<String, Double> allRates = new HashMap<>();
        allRates.put("EUR", 0.93);
        allRates.put("JPY", 149.50);
        allRates.put("KRW", 1340.0);
        allRates.put("BTC", 65000.0); // 주요 통화 아님 → 필터링 대상

        ExchangeRateApiResponse mockResponse = new ExchangeRateApiResponse();
        mockResponse.setResult("success");
        mockResponse.setBaseCode("USD");
        mockResponse.setRates(allRates);
        mockResponse.setTimeLastUpdateUtc("2024-01-01 00:00:00");
        mockResponse.setTimeNextUpdateUtc("2024-01-02 00:00:00");

        when(apiClient.getLatestRates("USD")).thenReturn(mockResponse);

        // when
        RatesResponse result = exchangeRateService.getRates("USD");

        // then
        assertThat(result.getBaseCurrency()).isEqualTo("USD");
        assertThat(result.getRates()).containsKeys("EUR", "JPY", "KRW");
        assertThat(result.getRates()).doesNotContainKey("BTC");
        verify(apiClient, times(1)).getLatestRates("USD");
    }

    @Test
    @DisplayName("통화 변환 - 지원하지 않는 통화 코드 예외 발생")
    void convert_shouldThrowException_whenUnsupportedCurrency() {
        // given
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.93);

        ExchangeRateApiResponse mockResponse = new ExchangeRateApiResponse();
        mockResponse.setResult("success");
        mockResponse.setBaseCode("USD");
        mockResponse.setRates(rates);

        when(apiClient.getLatestRates(anyString())).thenReturn(mockResponse);

        // when & then
        assertThatThrownBy(() -> exchangeRateService.convert("USD", "XYZ", 100.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 통화 코드");
    }
}
