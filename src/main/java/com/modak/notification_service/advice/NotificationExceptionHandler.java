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
        ErrorResponse errorResponse = new ErrorResponse(
                "Speed limit exceeded",
                "The speed limit for the type of notification" + e.getType() + " has been exceeded.",
                "Try again in " + e.getRetryAfterMillis() + " milliseconds."
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