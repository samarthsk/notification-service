package com.banking.notification.messaging;

import com.banking.notification.config.RabbitMQConfig;
import com.banking.notification.dto.AccountUpdateEvent;
import com.banking.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountUpdateListener {
    private final NotificationService notificationService;

    @RabbitListener(queues = "account.update.queue")
    public void handleAccountUpdate(AccountUpdateEvent event) {
        log.info("Received account update event: {}", event);
        notificationService.processAccountUpdateNotification(event);
    }
}
