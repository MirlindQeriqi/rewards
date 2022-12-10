package com.example.reward.web;

import com.example.reward.constants.PathConstants;
import com.example.reward.dto.TransactionDto;
import com.example.reward.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(PathConstants.TRANSACTIONS)
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public TransactionDto create(@RequestBody @Validated TransactionDto transactionDto) {
        return transactionService.create(transactionDto);
    }

    @GetMapping("/{id}")
    public TransactionDto getById(@PathVariable Long id) {
        return transactionService.getById(id);
    }

    @GetMapping
    public List<TransactionDto> getByDateRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                               @RequestParam Long customerId) {
        return transactionService.getByDateRangeAndCustomerId(startDate, endDate, customerId);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id){
        transactionService.delete(id);
    }
}
