package com.example.exchangerate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * ExchangeRate-API (open.er-api.com) 응답 DTO
 * API 문서: https://www.exchangerate-api.com/docs/free
 */
@Data
public class ExchangeRateApiResponse {

    private String result;            // "success" or "error"

    @JsonProperty("base_code")
    private String baseCode;          // 기준 통화 코드 (ex: "USD")

    private Map<String, Double> rates; // 환율 맵 (ex: {"KRW": 1340.5, "EUR": 0.93})

    @JsonProperty("time_last_update_unix")
    private Long timeLastUpdateUnix;  // 마지막 업데이트 타임스탬프

    @JsonProperty("time_last_update_utc")
    private String timeLastUpdateUtc; // 마지막 업데이트 UTC 문자열

    @JsonProperty("time_next_update_utc")
    private String timeNextUpdateUtc; // 다음 업데이트 UTC 문자열
}
