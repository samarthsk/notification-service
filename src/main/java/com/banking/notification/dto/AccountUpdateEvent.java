package com.banking.notification.dto;

import com.banking.notification.entity.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateEvent {
    private Long accountId;
    private Long customerId;
    private String accountNumber;
    private String accountType;
    private Double balance;
    private String currency;
    private AccountStatus status;
    private String createdAt;
}
