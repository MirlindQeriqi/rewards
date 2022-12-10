package com.example.reward.service;

import com.example.reward.core.repository.TransactionRepository;
import com.example.reward.dto.RewardsDto;
import com.example.reward.dto.TransactionDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class RewardsServiceTest {

    @Autowired
    private RewardsService rewardsService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Value("${rewards.minimum.amount}")
    private Integer minimumAmount;

    @AfterEach
    public void afterEach() {
        transactionRepository.deleteAll();
    }

    @Test
    void calculateBonusPointsWithDbData(){
        Long customerId = 50L;
        TransactionDto transactionDto = TransactionDto.builder()
                .amount(new BigDecimal(100))
                .customerId(customerId)
                .build();

        TransactionDto transactionDto2 = TransactionDto.builder()
                .amount(new BigDecimal(120))
                .customerId(customerId)
                .build();

        TransactionDto savedTransaction = transactionService.create(transactionDto);
        TransactionDto savedTransaction2 = transactionService.create(transactionDto2);

        Assertions.assertNotNull(savedTransaction.getId());
        Assertions.assertNotNull(savedTransaction2.getId());

        List<TransactionDto> transactionDtos = transactionService.getByCustomerIdAndDateRangeAndMinimumAmount(customerId,
                LocalDate.now(), LocalDate.now(), new BigDecimal(minimumAmount));

        RewardsDto rewardsDto = rewardsService.calculateRewardsForTransactions(transactionDtos);

        Assertions.assertEquals(rewardsDto.getPoints(), 90);
    }

    @Test
    void calculateBonusPointsWithReadyData(){
        Long customerId = 50L;
        TransactionDto transactionDto = TransactionDto.builder()
                .amount(new BigDecimal(100))
                .customerId(customerId)
                .build();

        TransactionDto transactionDto2 = TransactionDto.builder()
                .amount(new BigDecimal(120))
                .customerId(customerId)
                .build();

        RewardsDto rewardsDto = rewardsService.calculateRewardsForTransactions(List.of(transactionDto, transactionDto2));

        Assertions.assertEquals(rewardsDto.getPoints(), 90);
    }

    @Test
    void calculateBonusPointsWithReadyDataLowerPrices(){
        Long customerId = 50L;
        TransactionDto transactionDto = TransactionDto.builder()
                .amount(new BigDecimal(50))
                .customerId(customerId)
                .build();

        TransactionDto transactionDto2 = TransactionDto.builder()
                .amount(new BigDecimal(23))
                .customerId(customerId)
                .build();

        RewardsDto rewardsDto = rewardsService.calculateRewardsForTransactions(List.of(transactionDto, transactionDto2));

        Assertions.assertEquals(rewardsDto.getPoints(), 0);
    }
}
