package com.modak.notification_service.repositories;

import org.springframework.stereotype.Component;

@Component
public class Gateway {
    public void send(String userId, String message) {
        System.out.println("sending message to user " + userId + ": " + message);
    }
}

