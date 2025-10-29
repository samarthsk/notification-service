package com.banking.notification.controller;

import com.banking.notification.dto.*;
import com.banking.notification.entity.Notification;
import com.banking.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "APIs for managing notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/transaction")
    @Operation(summary = "Send transaction notification", description = "Sends notification for high-value transactions")
    public ResponseEntity<NotificationResponse> sendTransactionNotification(
            @RequestBody TransactionNotificationRequest request) {
        NotificationResponse response = notificationService.sendTransactionNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/account-status")
    @Operation(summary = "Send account status notification", description = "Sends notification for account status changes")
    public ResponseEntity<NotificationResponse> sendAccountStatusNotification(
            @RequestBody AccountUpdateEvent account) {
        NotificationResponse response = notificationService.sendAccountStatusNotification(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all notifications", description = "Retrieves all notifications from the database")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent notifications", description = "Retrieves the 10 most recent notifications")
    public ResponseEntity<List<Notification>> getRecentNotifications() {
        List<Notification> notifications = notificationService.getRecentNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID", description = "Retrieves a specific notification by its ID")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        Notification notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/retry-failed")
    @Operation(summary = "Retry failed notifications", description = "Retries sending all failed notifications")
    public ResponseEntity<String> retryFailedNotifications() {
        notificationService.retryFailedNotifications();
        return ResponseEntity.ok("Failed notifications retry initiated");
    }
}
