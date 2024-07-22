package com.modak.notification_service.advice;

import com.modak.notification_service.exceptions.ErrorResponse;
import com.modak.notification_service.exceptions.RateLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class NotificationExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(RateLimitExceededException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleRateLimitExceededException(RateLimitExceededException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Límite de velocidad excedido",
                "El límite de velocidad para el tipo de notificación " + e.getType() + " ha sido excedido.",
                "Intente nuevamente en " + e.getRetryAfterMillis() + " milisegundos."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
    }
}