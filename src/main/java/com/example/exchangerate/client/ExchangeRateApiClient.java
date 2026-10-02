package com.example.exchangerate.client;

import com.example.exchangerate.dto.ExchangeRateApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeRateApiClient {

    private final WebClient webClient;

    /**
     * 기준 통화 기준 최신 환율 조회
     * API: GET /latest/{baseCurrency}
     *
     * @param baseCurrency 기준 통화 코드 (예: "USD", "KRW")
     * @return ExchangeRateApiResponse
     */
    public ExchangeRateApiResponse getLatestRates(String baseCurrency) {
        log.debug("외부 API 호출: 기준 통화 = {}", baseCurrency);

        try {
            ExchangeRateApiResponse response = webClient.get()
                    .uri("/latest/{base}", baseCurrency.toUpperCase())
                    .retrieve()
                    .bodyToMono(ExchangeRateApiResponse.class)
                    .block();

            if (response == null || !"success".equals(response.getResult())) {
                throw new RuntimeException("환율 API 응답 오류: " + baseCurrency);
            }

            log.debug("API 응답 성공: {} 기준 {}개 통화", baseCurrency, response.getRates().size());
            return response;

        } catch (WebClientResponseException e) {
            log.error("환율 API 호출 실패: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("환율 API 호출 중 오류가 발생했습니다.", e);
        }
    }
}
