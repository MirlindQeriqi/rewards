package com.example.reward.service;

import com.example.reward.dto.TransactionDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    TransactionDto create(TransactionDto transactionDto);

    TransactionDto getById(Long id);

    void delete(Long id);

    List<TransactionDto> getByDateRangeAndCustomerId(LocalDate startDate, LocalDate endDate, Long customerId);

    List<TransactionDto> getByCustomerIdAndDateRangeAndMinimumAmount(Long customerId, LocalDate startDate, LocalDate endDate,
                                                                     BigDecimal minimumAmount);
}
