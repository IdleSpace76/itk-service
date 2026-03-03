package ru.task.itk_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * @author a.zharov
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(WalletNotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.builder()
                        .error(ex.getMessage())
                        .message("Счет не найден")
                        .timestamp(Instant.now())
                        .path(req.getRequestURI())
                        .build());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficient(InsufficientFundsException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.builder()
                        .error(ex.getMessage())
                        .message("Недостаточно средств")
                        .timestamp(Instant.now())
                        .path(req.getRequestURI())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.builder()
                        .error("VALIDATION_ERROR")
                        .message("Ошибка в параметрах запроса")
                        .timestamp(Instant.now())
                        .path(req.getRequestURI())
                        .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleBadJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.builder()
                        .error("INVALID_JSON")
                        .message("Неверный формат JSON")
                        .timestamp(Instant.now())
                        .path(req.getRequestURI())
                        .build());
    }

    // Ошибка scale
    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<ApiErrorResponse> handleArithmetic(ArithmeticException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.builder()
                        .error("INVALID_AMOUNT_SCALE")
                        .message("Сумма должна содержать 2 цифры после знака")
                        .timestamp(Instant.now())
                        .path(req.getRequestURI())
                        .build());
    }
}
