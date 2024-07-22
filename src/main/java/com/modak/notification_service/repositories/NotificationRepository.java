package com.modak.notification_service.repositories;

import com.modak.notification_service.entities.Notification;
import com.modak.notification_service.entities.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAddresseeAndNotificationTypeAndDateTimeAfter(String addressee, NotificationType notificationType, LocalDateTime dateTime);
}
