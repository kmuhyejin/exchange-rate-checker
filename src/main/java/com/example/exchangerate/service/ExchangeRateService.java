package com.example.exchangerate.service;

import com.example.exchangerate.client.ExchangeRateApiClient;
import com.example.exchangerate.dto.ConvertResponse;
import com.example.exchangerate.dto.ExchangeRateApiResponse;
import com.example.exchangerate.dto.RatesResponse;
import com.example.exchangerate.entity.ExchangeRateHistory;
import com.example.exchangerate.repository.ExchangeRateHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateApiClient apiClient;
    private final ExchangeRateHistoryRepository historyRepository;

    @Value("${exchange-rate.major-currencies}")
    private String majorCurrenciesConfig;

    /**
     * 주요 통화 목록 반환
     */
    public List<String> getMajorCurrencies() {
        return Arrays.asList(majorCurrenciesConfig.split(","));
    }

    /**
     * 기준 통화 기준 환율 조회 (캐싱 적용 - 10분)
     */
    @Cacheable(value = "exchangeRates", key = "#baseCurrency.toUpperCase()")
    public RatesResponse getRates(String baseCurrency) {
        log.info("환율 조회 (캐시 미스): 기준 통화 = {}", baseCurrency);

        ExchangeRateApiResponse apiResponse = apiClient.getLatestRates(baseCurrency);

        List<String> majorCurrencies = getMajorCurrencies();
        Map<String, Double> filteredRates = apiResponse.getRates().entrySet().stream()
                .filter(e -> majorCurrencies.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return RatesResponse.builder()
                .baseCurrency(apiResponse.getBaseCode())
                .rates(filteredRates)
                .lastUpdated(apiResponse.getTimeLastUpdateUtc())
                .nextUpdate(apiResponse.getTimeNextUpdateUtc())
                .build();
    }

    /**
     * 통화 변환 및 이력 저장
     */
    @Transactional
    public ConvertResponse convert(String from, String to, double amount) {
        log.info("통화 변환: {} {} → {}", amount, from, to);

        RatesResponse ratesResponse = getRates(from);
        Map<String, Double> rates = ratesResponse.getRates();

        if (!rates.containsKey(to.toUpperCase())) {
            throw new IllegalArgumentException("지원하지 않는 통화 코드입니다: " + to);
        }

        double rate = rates.get(to.toUpperCase());
        double result = Math.round(amount * rate * 100.0) / 100.0;

        // 조회 이력 저장
        ExchangeRateHistory history = ExchangeRateHistory.builder()
                .baseCurrency(from.toUpperCase())
                .targetCurrency(to.toUpperCase())
                .rate(rate)
                .inputAmount(amount)
                .convertedAmount(result)
                .queriedAt(LocalDateTime.now())
                .build();
        historyRepository.save(history);

        return ConvertResponse.builder()
                .from(from.toUpperCase())
                .to(to.toUpperCase())
                .amount(amount)
                .result(result)
                .rate(rate)
                .convertedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    /**
     * 최근 조회 이력 반환
     */
    @Transactional(readOnly = true)
    public List<ExchangeRateHistory> getHistory() {
        return historyRepository.findTop20ByOrderByQueriedAtDesc();
    }
}
