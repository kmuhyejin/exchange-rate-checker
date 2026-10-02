package com.example.exchangerate.controller;

import com.example.exchangerate.dto.ConvertResponse;
import com.example.exchangerate.dto.RatesResponse;
import com.example.exchangerate.entity.ExchangeRateHistory;
import com.example.exchangerate.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    /**
     * GET /api/exchange/rates?base=USD
     * 주요 통화 환율 목록 조회
     */
    @GetMapping("/rates")
    public ResponseEntity<RatesResponse> getRates(
            @RequestParam(defaultValue = "USD") String base) {
        log.debug("GET /api/exchange/rates?base={}", base);
        RatesResponse response = exchangeRateService.getRates(base);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/exchange/convert?from=USD&to=KRW&amount=100
     * 통화 변환
     */
    @GetMapping("/convert")
    public ResponseEntity<ConvertResponse> convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam double amount) {
        log.debug("GET /api/exchange/convert?from={}&to={}&amount={}", from, to, amount);
        ConvertResponse response = exchangeRateService.convert(from, to, amount);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/exchange/currencies
     * 지원 통화 목록 조회
     */
    @GetMapping("/currencies")
    public ResponseEntity<List<String>> getCurrencies() {
        return ResponseEntity.ok(exchangeRateService.getMajorCurrencies());
    }

    /**
     * GET /api/exchange/history
     * 최근 조회 이력
     */
    @GetMapping("/history")
    public ResponseEntity<List<ExchangeRateHistory>> getHistory() {
        return ResponseEntity.ok(exchangeRateService.getHistory());
    }

    /**
     * 전역 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        log.error("서버 오류: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body(Map.of("error", "환율 정보를 가져오는 데 실패했습니다."));
    }
}
