package com.modak.notification_service.services;

public interface NotificationService {
    void sendNotification(String type, String userId, String message);
}