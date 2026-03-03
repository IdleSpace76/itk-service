package ru.task.itk_service.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author a.zharov
 */
@Getter
@Builder
public class WalletBalanceResponse {

    private UUID walletId;
    private BigDecimal balance;
}
