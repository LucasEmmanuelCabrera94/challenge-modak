package com.modak.notification_service.services.impl;

import com.modak.notification_service.entities.Notification;
import com.modak.notification_service.entities.NotificationType;
import com.modak.notification_service.exceptions.InvalidNotificationTypeException;
import com.modak.notification_service.exceptions.RateLimitExceededException;
import com.modak.notification_service.repositories.Gateway;
import com.modak.notification_service.repositories.NotificationRepository;
import com.modak.notification_service.services.NotificationService;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    @Override
    public void sendNotification(String type, String addressee, String message) {
        LocalDateTime now = LocalDateTime.now();
        NotificationType notificationType;

        try {
            notificationType = NotificationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidNotificationTypeException(type);
        }

        if (!canSendNotification(addressee, notificationType, now)) {
            long timeRemaining = calculateTimeRemaining(addressee, notificationType, now);
            throw new RateLimitExceededException("Rate limit exceeded for " + type + " notifications", timeRemaining);
        }

        Notification notification = new Notification();
        notification.setAddressee(addressee);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        notification.setDateTime(now);
        notificationRepository.save(notification);

        gateway.send(addressee, message);
    }

    private boolean canSendNotification(String addressee, NotificationType type, LocalDateTime now) {
        List<Notification> recentNotifications = getRecentNotifications(addressee, type, now);
        int limit = getNotificationLimit(type);
        return recentNotifications.size() < limit;
    }

    private long calculateTimeRemaining(String addressee, NotificationType type, LocalDateTime now) {
        List<Notification> recentNotifications = getRecentNotifications(addressee, type, now);
        int limit = getNotificationLimit(type);

        if (recentNotifications.size() >= limit) {
            LocalDateTime lastNotificationTime = recentNotifications.get(0).getDateTime();
            LocalDateTime earliestAllowedTime = getEarliestAllowedTime(type, lastNotificationTime);
            return Duration.between(now, earliestAllowedTime).toSeconds();
        }

        return 0;
    }

    private int getNotificationLimit(NotificationType type) {
        return switch (type) {
            case STATUS -> 2;
            case NEWS -> 1;
            case MARKETING -> 3;
            default -> Integer.MAX_VALUE;
        };
    }

    private LocalDateTime getEarliestAllowedTime(NotificationType type, LocalDateTime lastNotificationTime) {
        return switch (type) {
            case STATUS -> lastNotificationTime.plusMinutes(1);
            case NEWS -> lastNotificationTime.plusDays(1);
            case MARKETING -> lastNotificationTime.plusHours(1);
            default -> LocalDateTime.now();
        };
    }

    private List<Notification> getRecentNotifications(String addressee, NotificationType type, LocalDateTime now) {
        LocalDateTime limit = getLimitDateTime(type, now);
        return notificationRepository.findByAddresseeAndNotificationTypeAndDateTimeAfter(addressee, type, limit);
    }

    private LocalDateTime getLimitDateTime(NotificationType type, LocalDateTime now) {
        return switch (type) {
            case STATUS -> now.minusMinutes(1);
            case NEWS -> now.minusDays(1);
            case MARKETING -> now.minusHours(1);
            default -> now;
        };
    }
}
