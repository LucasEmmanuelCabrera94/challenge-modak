package com.modak.notification_service.services.impl;

import com.modak.notification_service.entities.Notification;
import com.modak.notification_service.entities.NotificationType;
import com.modak.notification_service.exceptions.RateLimitExceededException;
import com.modak.notification_service.repositories.Gateway;
import com.modak.notification_service.repositories.NotificationRepository;
import com.modak.notification_service.services.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final Gateway gateway;

    public NotificationServiceImpl(Gateway gateway, NotificationRepository notificationRepository) {
        this.gateway = gateway;
        this.notificationRepository = notificationRepository;
    }

    public void sendNotification(String type, String addressee, String message) {
        LocalDateTime now = LocalDateTime.now();
        NotificationType notificationType = NotificationType.valueOf(type.toUpperCase()); //TODO: Validar que exista

        if (!canSendNotification(addressee, notificationType, now)) {
            throw new RateLimitExceededException("Rate limit exceeded for " + type + " notifications", 1); //TODO : cambiar ultimo valor
        }

        Notification notification = new Notification();
        notification.setAddressee(addressee);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        notification.setDateTime(now);
        notificationRepository.save(notification);

        // Lógica para enviar el correo electrónico
    }

    private boolean canSendNotification(String addressee, NotificationType type, LocalDateTime now) {
        LocalDateTime limit = now;

        switch (type) {
            case STATUS:
                limit = now.minusMinutes(1);
                break;
            case NEWS:
                limit = now.minusDays(1);
                break;
            case MARKETING:
                limit = now.minusHours(1);
                break;
            default:
                return true;
        }

        List<Notification> recentNotifications = notificationRepository.findByAddresseeAndNotificationTypeAndDateTimeAfter(addressee, type, limit);

        switch (type) {
            case STATUS:
                return recentNotifications.size() < 2;
            case NEWS:
                return recentNotifications.size() < 1;
            case MARKETING:
                return recentNotifications.size() < 3;
            default:
                return true;
        }
    }
}
