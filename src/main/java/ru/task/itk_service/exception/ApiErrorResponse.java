package ru.task.itk_service.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * @author a.zharov
 */
@Getter
@Builder
public class ApiErrorResponse {

    /**
     * Код ошибки
     */
    String error;

    /**
     * Сообщение
     */
    String message;

    /**
     * Время
     */
    Instant timestamp;

    /**
     * Адрес
     */
    String path;
}
