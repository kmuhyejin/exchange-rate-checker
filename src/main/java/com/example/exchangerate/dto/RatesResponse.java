package com.example.exchangerate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatesResponse {

    private String baseCurrency;
    private Map<String, Double> rates;
    private String lastUpdated;
    private String nextUpdate;
}
