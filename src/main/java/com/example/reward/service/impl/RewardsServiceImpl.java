package com.example.reward.service.impl;

import com.example.reward.dto.RewardsDto;
import com.example.reward.dto.TransactionDto;
import com.example.reward.service.RewardsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RewardsServiceImpl implements RewardsService {

    @Value("${rewards.minimum.amount}")
    private Integer minimumAmount;

    @Value("${rewards.minimum.points}")
    private Integer pointsForMinimumAmount;

    @Value("${rewards.maximum.amount}")
    private Integer maximumAmount;

    @Value("${rewards.maximum.points}")
    private Integer pointsForMaximumAmount;

    @Override
    public RewardsDto calculateRewardsForTransactions(List<TransactionDto> transactionDtos) {
        Integer points = 0;

        for (TransactionDto transactionDto : transactionDtos) {
            Integer amount = transactionDto.getAmount().intValue();
            Integer transactionPoints = 0;

            if (amount > maximumAmount) {
                transactionPoints = (amount - maximumAmount) * pointsForMaximumAmount;
            } else if (amount > minimumAmount){
                transactionPoints = (amount - minimumAmount) * pointsForMinimumAmount;
            }

            points = points + transactionPoints;
        }

        return RewardsDto.builder()
                .points(points)
                .build();
    }
}
