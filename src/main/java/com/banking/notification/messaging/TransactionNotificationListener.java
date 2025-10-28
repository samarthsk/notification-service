package com.banking.notification.messaging;

import com.banking.notification.config.RabbitMQConfig;
import com.banking.notification.dto.TransactionNotificationRequest;
import com.banking.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionNotificationListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.TRANSACTION_NOTIFICATION_QUEUE)
    public void handleTransactionNotification(TransactionNotificationRequest request) {
        log.info("Received transaction notification message: {}", request);

        try {
            notificationService.sendTransactionNotification(request);
            log.info("Transaction notification processed successfully");
        } catch (Exception e) {
            log.error("Error processing transaction notification", e);
        }
    }
}
