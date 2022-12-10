package com.example.reward.service;

import com.example.reward.dto.RewardsDto;
import com.example.reward.dto.TransactionDto;
import java.util.List;

public interface RewardsService {

    public RewardsDto calculateRewardsForTransactions(List<TransactionDto> transactionDtos);
}
