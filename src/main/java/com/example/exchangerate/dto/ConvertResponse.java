package com.example.exchangerate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConvertResponse {

    private String from;
    private String to;
    private double amount;
    private double result;
    private double rate;
    private String convertedAt;
}
