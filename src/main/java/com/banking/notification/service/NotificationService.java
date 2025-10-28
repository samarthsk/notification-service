package com.banking.notification.service;

import com.banking.notification.dto.*;
import com.banking.notification.entity.Notification;
import java.util.List;

public interface NotificationService {

    NotificationResponse sendTransactionNotification(TransactionNotificationRequest request);

    NotificationResponse sendAccountStatusNotification(AccountStatusChangeRequest request);

    List<Notification> getAllNotifications();

    List<Notification> getRecentNotifications();

    Notification getNotificationById(Long id);

    void retryFailedNotifications();
}
