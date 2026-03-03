package ru.task.itk_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.task.itk_service.dao.WalletRepository;
import ru.task.itk_service.domain.Wallet;
import ru.task.itk_service.dto.WalletOperationRequest;
import ru.task.itk_service.dto.WalletOperationResponse;
import ru.task.itk_service.dto.enums.OperationType;
import ru.task.itk_service.exception.InsufficientFundsException;
import ru.task.itk_service.exception.WalletNotFoundException;
import ru.task.itk_service.service.WalletService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

/**
 * @author a.zharov
 */
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private static final int SCALE = 2;

    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public WalletOperationResponse applyOperation(WalletOperationRequest req) {
        Wallet wallet = walletRepository.findByIdForUpdate(req.getWalletId())
                .orElseThrow(WalletNotFoundException::new);

        BigDecimal amount = normalize(req.getAmount());
        BigDecimal before = normalize(wallet.getBalance());

        BigDecimal after;

        if (req.getOperationType() == OperationType.DEPOSIT) {
            after = before.add(amount);
        }
        else {
            if (before.compareTo(amount) < 0) {
                throw new InsufficientFundsException();
            }
            after = before.subtract(amount);
        }

        wallet.setBalance(after);

        return WalletOperationResponse.builder()
                .walletId(wallet.getId())
                .operationType(req.getOperationType())
                .amount(amount)
                .balanceBefore(before)
                .balanceAfter(after)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID walletId) {
        return walletRepository.findById(walletId)
                .map(w -> normalize(w.getBalance()))
                .orElseThrow(WalletNotFoundException::new);
    }

    private BigDecimal normalize(BigDecimal v) {
        return Objects.requireNonNullElse(v, BigDecimal.ZERO).setScale(SCALE, RoundingMode.UNNECESSARY);
    }
}
