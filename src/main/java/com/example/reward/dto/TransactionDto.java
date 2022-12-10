package com.example.reward.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    private Long id;

    @NotNull
    private BigDecimal amount;

    private LocalDateTime date;

    @NotNull
    private Long customerId;
}
