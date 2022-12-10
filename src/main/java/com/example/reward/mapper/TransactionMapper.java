package com.example.reward.mapper;

import com.example.reward.core.entity.Transaction;
import com.example.reward.dto.TransactionDto;
import java.time.LocalDateTime;

public class TransactionMapper {

    public static Transaction toEntity(TransactionDto transactionDto) {
        return Transaction.builder()
                .amount(transactionDto.getAmount())
                .date(LocalDateTime.now())
                .customerId(transactionDto.getCustomerId())
                .build();
    }

    public static TransactionDto toDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .customerId(transaction.getCustomerId())
                .build();
    }
}
