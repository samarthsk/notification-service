package com.banking.notification.dto;

import com.banking.notification.entity.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatusChangeRequest {
    private Long accountId;
    private String accountNumber;
    private AccountStatus oldStatus;
    private AccountStatus newStatus;
    private String customerName;
    private String recipientEmail;
    private String recipientPhone;
}
