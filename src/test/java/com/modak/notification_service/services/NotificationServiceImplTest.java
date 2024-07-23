package com.modak.notification_service.services;

import com.modak.notification_service.entities.Notification;
import com.modak.notification_service.entities.NotificationType;
import com.modak.notification_service.exceptions.InvalidNotificationTypeException;
import com.modak.notification_service.exceptions.RateLimitExceededException;
import com.modak.notification_service.repositories.Gateway;
import com.modak.notification_service.repositories.NotificationRepository;
import com.modak.notification_service.services.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private Gateway gateway;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    public void testSendNotificationWithValidType() {
        String type = "STATUS";
        String addressee = "test@example.com";
        String message = "Test Message";

        when(notificationRepository.findByAddresseeAndNotificationTypeAndDateTimeAfter(anyString(), any(), any()))
                .thenReturn(Collections.emptyList());

        notificationService.sendNotification(type, addressee, message);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(gateway, times(1)).send(addressee, message);
    }

    @Test
    public void testSendNotificationWithInvalidType() {
        String type = "INVALID_TYPE";
        String addressee = "test@example.com";
        String message = "Test Message";

        InvalidNotificationTypeException exception = assertThrows(InvalidNotificationTypeException.class, () ->
                notificationService.sendNotification(type, addressee, message));

        assertEquals("Invalid notification type: " + type, exception.getMessage());
    }

    @Test
    public void testSendNotificationRateLimitExceededStatusCase() {
        String type = "STATUS";
        String addressee = "test@example.com";
        String message = "Test Message";
        LocalDateTime now = LocalDateTime.now();

        Notification existingNotification = new Notification();
        existingNotification.setDateTime(now.minusSeconds(30));
        existingNotification.setNotificationType(NotificationType.STATUS);
        Notification existingNotification2 = new Notification();
        existingNotification2.setDateTime(now.minusSeconds(30));
        existingNotification2.setNotificationType(NotificationType.STATUS);
        List<Notification> recentNotifications = List.of(existingNotification, existingNotification2);

        when(notificationRepository.findByAddresseeAndNotificationTypeAndDateTimeAfter(anyString(), any(), any()))
                .thenReturn(recentNotifications);

        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, () ->
                notificationService.sendNotification(type, addressee, message));

        assertTrue(exception.getMessage().contains("Rate limit exceeded for STATUS notifications"));
    }

    @Test
    public void testSendNotificationRateLimitExceededNewsCase() {
        String type = "NEWS";
        String addressee = "test@example.com";
        String message = "Test Message";
        LocalDateTime now = LocalDateTime.now();

        Notification existingNotification = new Notification();
        existingNotification.setDateTime(now.minusSeconds(30));
        existingNotification.setNotificationType(NotificationType.NEWS);
        List<Notification> recentNotifications = List.of(existingNotification);

        when(notificationRepository.findByAddresseeAndNotificationTypeAndDateTimeAfter(anyString(), any(), any()))
                .thenReturn(recentNotifications);

        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, () ->
                notificationService.sendNotification(type, addressee, message));

        assertTrue(exception.getMessage().contains("Rate limit exceeded for NEWS notifications"));
    }

    @Test
    public void testSendNotificationRateLimitExceededMarketingCase() {
        String type = "MARKETING";
        String addressee = "test@example.com";
        String message = "Test Message";
        LocalDateTime now = LocalDateTime.now();

        Notification existingNotification = new Notification();
        existingNotification.setDateTime(now.minusSeconds(30));
        existingNotification.setNotificationType(NotificationType.MARKETING);
        Notification existingNotification2 = new Notification();
        existingNotification2.setDateTime(now.minusSeconds(30));
        existingNotification2.setNotificationType(NotificationType.MARKETING);
        Notification existingNotification3 = new Notification();
        existingNotification3.setDateTime(now.minusSeconds(30));
        existingNotification3.setNotificationType(NotificationType.MARKETING);
        List<Notification> recentNotifications = List.of(existingNotification, existingNotification2,existingNotification3);

        when(notificationRepository.findByAddresseeAndNotificationTypeAndDateTimeAfter(anyString(), any(), any()))
                .thenReturn(recentNotifications);

        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, () ->
                notificationService.sendNotification(type, addressee, message));

        assertTrue(exception.getMessage().contains("Rate limit exceeded for MARKETING notifications"));
    }

}

