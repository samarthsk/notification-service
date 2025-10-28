package com.banking.notification.service;

import com.banking.notification.dto.*;
import com.banking.notification.entity.*;
import com.banking.notification.repository.NotificationRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final MetricsService metricsService;
    private final MeterRegistry meterRegistry;

    // High-value transaction threshold (₹50,000)
    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("50000");

    @Override
    @Transactional
    public NotificationResponse sendTransactionNotification(TransactionNotificationRequest request) {
        log.info("Processing transaction notification for transaction ID: {}", request.getTransactionId());

        // Check if transaction is high-value
        boolean isHighValue = request.getAmount().compareTo(HIGH_VALUE_THRESHOLD) >= 0;

        if (!isHighValue) {
            log.info("Transaction amount {} is below threshold. Skipping notification.", request.getAmount());
            return new NotificationResponse(null, NotificationStatus.PENDING, "Below threshold, no notification sent");
        }

        // Create notification entity
        Notification notification = new Notification();
        notification.setRecipientEmail(maskEmail(request.getRecipientEmail()));
        notification.setRecipientPhone(maskPhone(request.getRecipientPhone()));
        notification.setNotificationType(NotificationType.HIGH_VALUE_TRANSACTION);
        notification.setChannel(NotificationChannel.EMAIL);
        notification.setReferenceId(request.getTransactionId());

        String subject = "High-Value Transaction Alert - ₹" + request.getAmount();
        String message = buildTransactionMessage(request);

        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setStatus(NotificationStatus.PENDING);

        // Save to database first
        notification = notificationRepository.save(notification);

        // Try to send email
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            sendEmail(request.getRecipientEmail(), subject, message);
            metricsService.incrementNotificationsSent("transaction");
            sample.stop(metricsService.getNotificationLatencyTimer());
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            log.info("Successfully sent notification for transaction ID: {}", request.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to send notification for transaction ID: {}", request.getTransactionId(), e);
            metricsService.incrementNotificationsFailed("transaction");
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notification.setFailedAt(LocalDateTime.now());
        }

        // Update notification status
        notification = notificationRepository.save(notification);

        return new NotificationResponse(
                notification.getId(),
                notification.getStatus(),
                "Notification processed"
        );
    }

    @Override
    @Transactional
    public NotificationResponse sendAccountStatusNotification(AccountStatusChangeRequest request) {
        log.info("Processing account status change notification for account: {}", request.getAccountNumber());

        // Create notification entity
        Notification notification = new Notification();
        notification.setRecipientEmail(maskEmail(request.getRecipientEmail()));
        notification.setRecipientPhone(maskPhone(request.getRecipientPhone()));
        notification.setNotificationType(determineAccountNotificationType(request.getNewStatus()));
        notification.setChannel(NotificationChannel.EMAIL);
        notification.setReferenceId(request.getAccountId());

        String subject = "Account Status Update - " + request.getAccountNumber();
        String message = buildAccountStatusMessage(request);

        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setStatus(NotificationStatus.PENDING);

        // Save to database
        notification = notificationRepository.save(notification);

        // Try to send email
        try {
            sendEmail(request.getRecipientEmail(), subject, message);
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            log.info("Successfully sent account status notification for account: {}", request.getAccountNumber());
        } catch (Exception e) {
            log.error("Failed to send account status notification: {}", request.getAccountNumber(), e);
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notification.setFailedAt(LocalDateTime.now());
        }

        // Update notification status
        notification = notificationRepository.save(notification);

        return new NotificationResponse(
                notification.getId(),
                notification.getStatus(),
                "Account status notification processed"
        );
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getRecentNotifications() {
        return notificationRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + id));
    }

    @Override
    @Transactional
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository.findByStatus(NotificationStatus.FAILED);

        log.info("Retrying {} failed notifications", failedNotifications.size());

        for (Notification notification : failedNotifications) {
            try {
                sendEmail(
                        unmaskEmail(notification.getRecipientEmail()),
                        notification.getSubject(),
                        notification.getMessage()
                );
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
            } catch (Exception e) {
                log.error("Retry failed for notification ID: {}", notification.getId(), e);
            }
        }
    }

    // Helper methods
    @Retryable(
            value = {MailException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("noreply@banking.com");

        mailSender.send(message);
    }

    private String buildTransactionMessage(TransactionNotificationRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

        return String.format("""
                Dear %s,
                
                This is to inform you about a high-value transaction on your account.
                
                Transaction Details:
                - Account Number: %s
                - Amount: ₹%s
                - Transaction Type: %s
                - Transaction ID: %s
                - Date & Time: %s
                
                If you did not authorize this transaction, please contact us immediately.
                
                Best Regards,
                Banking Team
                """,
                request.getCustomerName(),
                maskAccountNumber(request.getAccountNumber()),
                request.getAmount(),
                request.getTransactionType(),
                request.getTransactionId(),
                LocalDateTime.now().format(formatter)
        );
    }

    private String buildAccountStatusMessage(AccountStatusChangeRequest request) {
        return String.format("""
                Dear %s,
                
                Your account status has been updated.
                
                Account Details:
                - Account Number: %s
                - Previous Status: %s
                - New Status: %s
                
                If you have any questions, please contact our customer service.
                
                Best Regards,
                Banking Team
                """,
                request.getCustomerName(),
                maskAccountNumber(request.getAccountNumber()),
                request.getOldStatus(),
                request.getNewStatus()
        );
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return accountNumber;
        }
        return "XXXX-XXXX-" + accountNumber.substring(accountNumber.length() - 4);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String masked = username.substring(0, Math.min(2, username.length())) + "***";
        return masked + "@" + parts[1];
    }

    private String unmaskEmail(String maskedEmail) {
        // This is a placeholder - in production, you'd store unmasked email separately
        return maskedEmail;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }
        return "******" + phone.substring(phone.length() - 4);
    }

    private NotificationType determineAccountNotificationType(AccountStatus status) {
        return switch (status) {
            case FROZEN -> NotificationType.ACCOUNT_FROZEN;
            case ACTIVE -> NotificationType.ACCOUNT_ACTIVATED;
            default -> NotificationType.ACCOUNT_STATUS_CHANGE;
        };
    }

}
