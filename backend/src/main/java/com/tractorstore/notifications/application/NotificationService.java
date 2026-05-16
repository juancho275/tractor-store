package com.tractorstore.notifications.application;

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
import java.text.Normalizer;

/**
 * Sends order confirmation emails when an OrderPlaced event is received.
 *
 * <p>Works in two modes:
 * <ul>
 *   <li><b>Dev / no SMTP</b>: logs the full email body to console (no config needed)</li>
 *   <li><b>Prod</b>: sends real email via SMTP configured through Railway env vars</li>
 * </ul>
 *
 * <p>JavaMailSender is injected as optional — when spring.mail.host is not set,
 * Spring Boot does not create the bean and the service gracefully falls back to logging.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:onboarding@resend.dev}")
    private String from;

    public NotificationService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrderConfirmation(OrderPlaced event) {
        String subject = "Order Confirmation #" + event.orderNumber() + " - The Tractor Store";
        logPreview(event);

        if (mailSender != null) {
            trySendEmail(event.customerEmail(), subject, buildHtmlBody(event));
        } else {
            log.info("[Notifications] SMTP not configured - email preview shown above");
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
            log.warn("[Notifications] SMTP send failed - email preview shown in logs. Cause: {}", e.getMessage());
        }
    }

    // ASCII-safe log preview — avoids encoding issues on Windows consoles
    private void logPreview(OrderPlaced event) {
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

    // Full HTML body sent via SMTP — UTF-8 rendered correctly in email clients
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
