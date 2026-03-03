package ru.task.itk_service.dto;

import lombok.Builder;
import lombok.Getter;
import ru.task.itk_service.dto.enums.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author a.zharov
 */
@Getter
@Builder
public class WalletOperationResponse {

    private UUID walletId;
    private OperationType operationType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
}
