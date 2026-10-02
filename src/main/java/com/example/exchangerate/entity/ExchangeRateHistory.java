package com.example.exchangerate.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rate_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String baseCurrency;

    @Column(nullable = false, length = 10)
    private String targetCurrency;

    @Column(nullable = false)
    private Double rate;

    @Column
    private Double inputAmount;

    @Column
    private Double convertedAmount;

    @Column(nullable = false)
    private LocalDateTime queriedAt;

    @PrePersist
    public void prePersist() {
        this.queriedAt = LocalDateTime.now();
    }
}
