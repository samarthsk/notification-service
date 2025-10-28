package com.banking.notification.dto;

import com.banking.notification.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionNotificationRequest {
    private Long transactionId;
    private Long accountId;
    private String accountNumber;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String recipientEmail;
    private String recipientPhone;
    private String customerName;
}
