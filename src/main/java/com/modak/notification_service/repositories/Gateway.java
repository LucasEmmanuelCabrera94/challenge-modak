package com.modak.notification_service.repositories;

import org.springframework.stereotype.Component;

@Component
public class Gateway {
    //This class simulates the service that sends the mails.
    public void send(String userId, String message) {
        System.out.println("sending message to user " + userId + ": " + message);
    }
}

