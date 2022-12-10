package com.example.reward.service;

import com.example.reward.core.repository.TransactionRepository;
import com.example.reward.dto.TransactionDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class TransactionServiceTests {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionRepository transactionRepository;

    @Value("${rewards.minimum.amount}")
    private Integer minimumAmount;

    @AfterEach
    public void afterEach(){
        transactionRepository.deleteAll();
    }

    @Test
    void testCreateNewTransaction() {
        TransactionDto transactionDto = TransactionDto.builder()
                .amount(new BigDecimal(100))
                .customerId(1L)
                .build();

        TransactionDto savedTransaction = transactionService.create(transactionDto);

        Assertions.assertNotNull(savedTransaction.getId());
        Assertions.assertEquals(transactionDto.getAmount(), savedTransaction.getAmount());
        Assertions.assertEquals(transactionDto.getCustomerId(), savedTransaction.getCustomerId());
    }

    @Test
    void testGetTransactionById() {
        TransactionDto transactionDto = TransactionDto.builder()
                .amount(new BigDecimal(100))
                .customerId(1L)
                .build();

        TransactionDto savedTransaction = transactionService.create(transactionDto);

        Assertions.assertNotNull(savedTransaction.getId());

        TransactionDto foundTransaction = transactionService.getById(savedTransaction.getId());

        Assertions.assertNotNull(foundTransaction.getId());
    }

    @Test
    void testGetTransactionByIdNotFound() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> transactionService.getById(2L));
    }

    @Test
    void testGetTransactionsByCustomerIdAndDateRange() {
        Long customerId = 50L;
        TransactionDto transactionDto = TransactionDto.builder()
                .amount(new BigDecimal(100))
                .customerId(customerId)
                .build();

        TransactionDto transactionDto2 = TransactionDto.builder()
                .amount(new BigDecimal(150))
                .customerId(customerId)
                .build();

        TransactionDto savedTransaction = transactionService.create(transactionDto);
        TransactionDto savedTransaction2 = transactionService.create(transactionDto2);

        Assertions.assertNotNull(savedTransaction.getId());
        Assertions.assertNotNull(savedTransaction2.getId());

        List<TransactionDto> transactionDtoList = transactionService.getByDateRangeAndCustomerId(LocalDate.now(),
                LocalDate.now(), customerId);

        Assertions.assertEquals(transactionDtoList.size(), 2);
    }

    @Test
    void testGetTransactionsByCustomerIdAndWrongDateRange() {
        Long customerId = 50L;
        TransactionDto transactionDto = TransactionDto.builder()
                .amount(new BigDecimal(100))
                .customerId(customerId)
                .build();

        TransactionDto transactionDto2 = TransactionDto.builder()
                .amount(new BigDecimal(150))
                .customerId(customerId)
                .build();

        TransactionDto savedTransaction = transactionService.create(transactionDto);
        TransactionDto savedTransaction2 = transactionService.create(transactionDto2);

        Assertions.assertNotNull(savedTransaction.getId());
        Assertions.assertNotNull(savedTransaction2.getId());

        List<TransactionDto> transactionDtoList = transactionService.getByDateRangeAndCustomerId(
                LocalDate.now().minusDays(3), LocalDate.now().minusDays(1), customerId);

        Assertions.assertEquals(transactionDtoList.size(), 0);
    }

    @Test
    void testGetTransactionsByCustomerIdAndDateRangeAndMinimumAmount() {
        Long customerId = 50L;
        TransactionDto transactionDto = TransactionDto.builder()
                .amount(new BigDecimal(50))
                .customerId(customerId)
                .build();

        TransactionDto transactionDto2 = TransactionDto.builder()
                .amount(new BigDecimal(60))
                .customerId(customerId)
                .build();

        TransactionDto transactionDto3 = TransactionDto.builder()
                .amount(new BigDecimal(150))
                .customerId(customerId)
                .build();

        TransactionDto transactionDto4 = TransactionDto.builder()
                .amount(new BigDecimal(30))
                .customerId(customerId)
                .build();

        TransactionDto savedTransaction = transactionService.create(transactionDto);
        TransactionDto savedTransaction2 = transactionService.create(transactionDto2);
        TransactionDto savedTransaction3 = transactionService.create(transactionDto3);
        TransactionDto savedTransaction4 = transactionService.create(transactionDto4);

        Assertions.assertNotNull(savedTransaction.getId());
        Assertions.assertNotNull(savedTransaction2.getId());
        Assertions.assertNotNull(savedTransaction3.getId());
        Assertions.assertNotNull(savedTransaction4.getId());

        List<TransactionDto> transactionDtoList = transactionService.getByCustomerIdAndDateRangeAndMinimumAmount(
                customerId, LocalDate.now(), LocalDate.now(), new BigDecimal(minimumAmount));

        Assertions.assertEquals(transactionDtoList.size(), 2);
    }

    @Test
    void testDeleteTransactionById() {
        TransactionDto transactionDto = TransactionDto.builder()
                .amount(new BigDecimal(100))
                .customerId(1L)
                .build();

        TransactionDto savedTransaction = transactionService.create(transactionDto);

        Assertions.assertNotNull(savedTransaction.getId());

        transactionService.delete(savedTransaction.getId());

        Assertions.assertTrue(transactionRepository.findById(savedTransaction.getId()).isEmpty());
    }

    @Test
    void testDeleteTransactionByIdNotFound() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> transactionService.delete(2L));
    }
}
