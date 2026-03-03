package ru.task.itk_service.service;

import ru.task.itk_service.dto.WalletOperationRequest;
import ru.task.itk_service.dto.WalletOperationResponse;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author a.zharov
 */
public interface WalletService {

    /**
     * Выполнить операцию
     */
    WalletOperationResponse applyOperation(WalletOperationRequest req);

    /**
     * Получить баланс по уид
     */
    BigDecimal getBalance(UUID walletId);
}
