package com.example.reward.core.repository;

import com.example.reward.core.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCustomerIdAndDateBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByCustomerIdAndDateIsBetweenAndAmountIsGreaterThan(Long customerId, LocalDateTime startDate,
                                                                           LocalDateTime endDate, BigDecimal amount);
}
