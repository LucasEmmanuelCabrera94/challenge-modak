package com.modak.notification_service.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GatewayTest {
    @Mock
    private Gateway gateway;

    @Test
    public void testSend() {
        String userId = "testUser";
        String message = "Test Message";

        gateway.send(userId, message);

        verify(gateway, times(1)).send(userId, message);
    }
}
