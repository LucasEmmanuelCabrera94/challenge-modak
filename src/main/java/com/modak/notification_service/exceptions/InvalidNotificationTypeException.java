package com.modak.notification_service.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidNotificationTypeException extends RuntimeException {
    private String type;

    public InvalidNotificationTypeException(String type) {
        super("Invalid notification type: " + type);
        this.type = type;
    }
}
