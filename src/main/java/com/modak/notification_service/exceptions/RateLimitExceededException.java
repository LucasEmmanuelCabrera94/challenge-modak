package com.modak.notification_service.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RateLimitExceededException extends RuntimeException {
    private String type;
    private long retryAfterMillis;

    public RateLimitExceededException(String type, long retryAfterMillis) {
        super("Speed limit exceeded for the notification type: " + type);
        this.type = type;
        this.retryAfterMillis = retryAfterMillis;
    }

}
