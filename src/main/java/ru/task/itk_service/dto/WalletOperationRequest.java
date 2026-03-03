package ru.task.itk_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.task.itk_service.dto.enums.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author a.zharov
 */
@Data
public class WalletOperationRequest {

    @NotNull
    private UUID walletId;

    @NotNull
    private OperationType operationType;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
}
