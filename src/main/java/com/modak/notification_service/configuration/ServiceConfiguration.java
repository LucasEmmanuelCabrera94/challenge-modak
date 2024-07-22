package com.modak.notification_service.configuration;

import com.modak.notification_service.repositories.Gateway;
import com.modak.notification_service.repositories.NotificationRepository;
import com.modak.notification_service.services.NotificationService;
import com.modak.notification_service.services.impl.NotificationServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {
    @Bean
    public NotificationService notificationService(Gateway gateway, NotificationRepository notificationRepository){
        return new NotificationServiceImpl(gateway,notificationRepository);
    }
}
