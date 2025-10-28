package com.banking.notification.repository;

import com.banking.notification.entity.Notification;
import com.banking.notification.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByRecipientEmail(String recipientEmail);

    List<Notification> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Long countByStatus(NotificationStatus status);

    List<Notification> findTop10ByOrderByCreatedAtDesc();
}
