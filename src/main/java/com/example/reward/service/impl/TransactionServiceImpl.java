package com.example.reward.service.impl;

import com.example.reward.core.entity.Transaction;
import com.example.reward.core.repository.TransactionRepository;
import com.example.reward.dto.TransactionDto;
import com.example.reward.mapper.TransactionMapper;
import com.example.reward.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public TransactionDto create(TransactionDto transactionDto) {
        Transaction transaction = TransactionMapper.toEntity(transactionDto);

        transaction = transactionRepository.save(transaction);

        return TransactionMapper.toDto(transaction);
    }

    @Override
    public TransactionDto getById(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);

        if (transaction.isPresent()) {
            return TransactionMapper.toDto(transaction.get());
        }

        throw new EntityNotFoundException("No transaction was found with id: " + id);
    }

    @Override
    public void delete(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);

        transaction.ifPresentOrElse(transactionRepository::delete, () -> {
            throw new EntityNotFoundException("No transaction was found for deletion with id: " + id);
        });
    }

    @Override
    public List<TransactionDto> getByDateRangeAndCustomerId(LocalDate startDate, LocalDate endDate, Long customerId) {
        List<Transaction> transactions = transactionRepository.findByCustomerIdAndDateBetween(customerId,
                startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));

        return transactions.stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDto> getByCustomerIdAndDateRangeAndMinimumAmount(Long customerId, LocalDate startDate, LocalDate endDate, BigDecimal minimumAmount) {
        List<Transaction> transactions =  transactionRepository.findByCustomerIdAndDateIsBetweenAndAmountIsGreaterThan(
                customerId, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), minimumAmount);

        return transactions.stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
    }
}
