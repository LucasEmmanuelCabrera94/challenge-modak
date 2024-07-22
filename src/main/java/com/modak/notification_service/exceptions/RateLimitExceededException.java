package com.modak.notification_service.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RateLimitExceededException extends RuntimeException {
    private String type;
    private long retryAfterMillis;

    public RateLimitExceededException(String type, long retryAfterMillis) {
        super("Límite de velocidad excedido para el tipo de notificación: " + type);
        this.type = type;
        this.retryAfterMillis = retryAfterMillis;
    }

    public String getType() {
        return type;
    }

    public long getRetryAfterMillis() {
        return retryAfterMillis;
    }

}
