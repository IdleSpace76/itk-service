package ru.task.itk_service.exception;

/**
 * @author a.zharov
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super("INSUFFICIENT_FUNDS");
    }
}
