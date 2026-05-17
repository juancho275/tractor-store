package com.tractorstore.notifications.application;

import com.tractorstore.order.application.event.OrderPlaced;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("NotificationService — order confirmation email logic")
class NotificationServiceTest {

    private OrderPlaced sampleEvent() {
        return new OrderPlaced(
            UUID.randomUUID(),
            "TS-99999-1002",
            "test@example.com",
            new BigDecimal("150000000"),
            List.of(
                new OrderPlaced.OrderPlacedItem(
                    UUID.randomUUID(),
                    "Tractor Autonomo X1",
                    "Verde - GPS incluido",
                    new BigDecimal("150000000"),
                    1
                )
            )
        );
    }

    @Test
    @DisplayName("logs email preview without throwing when no mailSender configured")
    void sendOrderConfirmation_noMailSender_logsAndDoesNotThrow() {
        NotificationService service = new NotificationService(null);
        assertDoesNotThrow(() -> service.sendOrderConfirmation(sampleEvent()));
    }

    @Test
    @DisplayName("sends email via SMTP when mailSender is configured")
    void sendOrderConfirmation_withMailSender_sendsEmail() {
        JavaMailSender mockSender = mock(JavaMailSender.class);
        MimeMessage realMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mockSender.createMimeMessage()).thenReturn(realMessage);

        NotificationService service = new NotificationService(mockSender);
        ReflectionTestUtils.setField(service, "from", "test@example.com");
        service.sendOrderConfirmation(sampleEvent());

        verify(mockSender, times(1)).send(realMessage);
    }

    @Test
    @DisplayName("does not propagate exception when SMTP send fails")
    void sendOrderConfirmation_smtpThrows_doesNotPropagate() {
        JavaMailSender mockSender = mock(JavaMailSender.class);
        MimeMessage realMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mockSender.createMimeMessage()).thenReturn(realMessage);
        doThrow(new RuntimeException("SMTP connection refused")).when(mockSender).send(any(MimeMessage.class));

        NotificationService service = new NotificationService(mockSender);
        ReflectionTestUtils.setField(service, "from", "test@example.com");
        assertDoesNotThrow(() -> service.sendOrderConfirmation(sampleEvent()));
    }
}
