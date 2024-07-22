package com.modak.notification_service.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationRequest {
    private String type;
    private String userId;
    private String message;
}
