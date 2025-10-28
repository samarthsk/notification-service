package com.banking.notification.messaging;

import com.banking.notification.config.RabbitMQConfig;
import com.banking.notification.dto.AccountStatusChangeRequest;
import com.banking.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountStatusListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.ACCOUNT_STATUS_QUEUE)
    public void handleAccountStatusChange(AccountStatusChangeRequest request) {
        log.info("Received account status change message: {}", request);

        try {
            notificationService.sendAccountStatusNotification(request);
            log.info("Account status notification processed successfully");
        } catch (Exception e) {
            log.error("Error processing account status notification", e);
        }
    }
}
