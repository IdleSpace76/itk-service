package ru.task.itk_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.task.itk_service.dto.WalletBalanceResponse;
import ru.task.itk_service.dto.WalletOperationRequest;
import ru.task.itk_service.dto.WalletOperationResponse;
import ru.task.itk_service.service.WalletService;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author a.zharov
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/wallet")
    public WalletOperationResponse operate(@RequestBody @Valid WalletOperationRequest req) {
        return walletService.applyOperation(req);
    }

    @GetMapping("/wallets/{walletId}")
    public WalletBalanceResponse getBalance(@PathVariable UUID walletId) {
        BigDecimal balance = walletService.getBalance(walletId);
        return WalletBalanceResponse.builder()
                .walletId(walletId)
                .balance(balance)
                .build();
    }
}
