package com.modak.notification_service.advice;

import com.modak.notification_service.exceptions.ErrorResponse;
import com.modak.notification_service.exceptions.InvalidNotificationTypeException;
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
        long seconds = e.getTimeRemaining();
        long minutes = (seconds / 60) % 60;
        long hours = (seconds / 60 / 60) % 24;
        long days = seconds / 60 / 60 / 24;
        seconds = seconds % 60;

        String retryAfterMessage;
        if (days > 0) {
            retryAfterMessage = String.format("Try again in %d days, %d hours, %d minutes, and %d seconds.", days, hours, minutes, seconds);
        } else if (hours > 0) {
            retryAfterMessage = String.format("Try again in %d hours, %d minutes, and %d seconds.", hours, minutes, seconds);
        } else if (minutes > 0) {
            retryAfterMessage = String.format("Try again in %d minutes and %d seconds.", minutes, seconds);
        } else {
            retryAfterMessage = String.format("Try again in %d seconds.", seconds);
        }

        ErrorResponse errorResponse = new ErrorResponse(
                "Speed limit exceeded",
                "The speed limit for the type of notification " + e.getType() + " has been exceeded.",
                retryAfterMessage
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(InvalidNotificationTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidNotificationTypeException(InvalidNotificationTypeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid notification type",
                "The notification type: " + ex.getType() + " is invalid.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}