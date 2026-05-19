package com.tractorstore.notifications.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractorstore.order.application.event.OrderPlaced;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:onboarding@resend.dev}")
    private String from;

    // Set via RESEND_API_KEY env var in Railway — preferred over SMTP
    @Value("${resend.api.key:}")
    private String resendApiKey;

    public NotificationService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrderConfirmation(OrderPlaced event) {
        String subject = "Order Confirmation #" + event.orderNumber() + " - The Tractor Store";
        logPreview(event);

        if (resendApiKey != null && !resendApiKey.isEmpty()) {
            trySendViaResendApi(event.customerEmail(), subject, buildHtmlBody(event));
        } else if (mailSender != null) {
            trySendEmail(event.customerEmail(), subject, buildHtmlBody(event));
        } else {
            log.info("[Notifications] No email transport configured - email preview shown above");
        }
    }

    private void trySendViaResendApi(String to, String subject, String htmlBody) {
        try {
            String json = new ObjectMapper().writeValueAsString(Map.of(
                "from", from,
                "to", List.of(to),
                "subject", subject,
                "html", htmlBody
            ));

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer " + resendApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .timeout(Duration.ofSeconds(30))
                .build();

            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    log.info("[Notifications] Email sent via Resend API to {}", to);
                } else if (log.isWarnEnabled()) {
                    log.warn("[Notifications] Resend API returned {}: {}", response.statusCode(), response.body());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[Notifications] Resend API call interrupted", e);
        } catch (Exception e) {
            log.warn("[Notifications] Resend API call failed", e);
        }
    }

    private void trySendEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("[Notifications] Email sent successfully to {}", to);
        } catch (Exception e) {
            log.warn("[Notifications] SMTP send failed - email preview shown in logs", e);
        }
    }

    // ASCII-safe log preview — avoids encoding issues on Windows consoles
    private void logPreview(OrderPlaced event) {
        if (!log.isInfoEnabled()) return;
        StringBuilder items = new StringBuilder();
        for (OrderPlaced.OrderPlacedItem item : event.items()) {
            BigDecimal subtotal = item.unitPrice().multiply(BigDecimal.valueOf(item.quantity()));
            items.append(String.format("  -> %s (%s) x%d = COP %s%n",
                ascii(item.productName()), ascii(item.variantName()),
                item.quantity(), formatCop(subtotal)));
        }

        log.info("""

            ========================================================
                     ORDER CONFIRMATION EMAIL - preview
            ========================================================
              To      : {}
              Order   : {}
              Total   : COP {}
            --------------------------------------------------------
            {}
            ========================================================
            """,
            event.customerEmail(),
            event.orderNumber(),
            formatCop(event.total()),
            items
        );
    }

    private String buildHtmlBody(OrderPlaced event) {
        StringBuilder rows = new StringBuilder();
        for (OrderPlaced.OrderPlacedItem item : event.items()) {
            BigDecimal subtotal = item.unitPrice().multiply(BigDecimal.valueOf(item.quantity()));
            rows.append(String.format(
                "<tr><td>%s (%s)</td><td>%d</td><td>COP %s</td></tr>",
                item.productName(), item.variantName(), item.quantity(), formatCop(subtotal)));
        }

        return """
            <html><body style="font-family:sans-serif;color:#333">
              <h2>Gracias por tu compra en <strong>The Tractor Store</strong></h2>
              <p>N&uacute;mero de orden: <strong>%s</strong></p>
              <table border="1" cellpadding="8" cellspacing="0" style="border-collapse:collapse">
                <thead><tr><th>Producto</th><th>Cantidad</th><th>Subtotal</th></tr></thead>
                <tbody>%s</tbody>
              </table>
              <p><strong>Total: COP %s</strong></p>
              <p>Tu pedido est&aacute; siendo procesado. Te notificaremos cuando sea despachado.</p>
              <hr><p style="color:#888">El equipo de The Tractor Store</p>
            </body></html>
            """.formatted(event.orderNumber(), rows, formatCop(event.total()));
    }

    private String formatCop(BigDecimal amount) {
        return String.format("%,.0f", amount);
    }

    /** Strips diacritics for ASCII-safe console output (e.g. "Estándar" → "Estandar"). */
    private String ascii(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                         .replaceAll("[^\\p{ASCII}]", "");
    }
}
