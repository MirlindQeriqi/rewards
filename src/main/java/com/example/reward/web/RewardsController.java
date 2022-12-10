package com.example.reward.web;

import com.example.reward.constants.PathConstants;
import com.example.reward.dto.RewardsDto;
import com.example.reward.dto.TransactionDto;
import com.example.reward.service.RewardsService;
import com.example.reward.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(PathConstants.REWARDS)
@RequiredArgsConstructor
public class RewardsController {

    private final RewardsService rewardsService;
    private final TransactionService transactionService;

    @Value("${rewards.minimum.amount}")
    private Integer minimumAmount;

    //Use this API if you want to query the data from the DB, and calculate Points
    @GetMapping
    public RewardsDto calculateRewards(@RequestParam Long customerId,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        List<TransactionDto> transactions = transactionService.getByCustomerIdAndDateRangeAndMinimumAmount(customerId,
                startDate, endDate, new BigDecimal(minimumAmount));

        return rewardsService.calculateRewardsForTransactions(transactions);
    }

    //Use this API if you have transaction data ready, and you just want to get the Points directly
    @PostMapping
    public RewardsDto calculateRewardsForReadyData(@RequestBody List<TransactionDto> transactionDtos){
        return rewardsService.calculateRewardsForTransactions(transactionDtos);
    }
}
