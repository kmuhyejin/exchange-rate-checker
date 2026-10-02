package com.example.exchangerate.repository;

import com.example.exchangerate.entity.ExchangeRateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRateHistoryRepository extends JpaRepository<ExchangeRateHistory, Long> {

    // 최근 20개 이력 (최신순)
    List<ExchangeRateHistory> findTop20ByOrderByQueriedAtDesc();

    // 특정 통화 쌍 이력
    List<ExchangeRateHistory> findByBaseCurrencyAndTargetCurrencyOrderByQueriedAtDesc(
            String baseCurrency, String targetCurrency);
}
