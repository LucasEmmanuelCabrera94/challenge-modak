package com.modak.notification_service.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private String description;
    private String recommendation;

    public ErrorResponse(String message, String description, String recommendation) {
        this.message = message;
        this.description = description;
        this.recommendation = recommendation;
    }
}