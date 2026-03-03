package ru.task.itk_service.exception;

/**
 * @author a.zharov
 */
public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException() {
        super("WALLET_NOT_FOUND");
    }
}
