package com.banking.notification.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerServiceClient {
    @Value("${customer.service.base-url}")
    private String customerServiceBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public CustomerDetails fetchCustomerById(Long customerId) {
        String url = customerServiceBaseUrl + "/api/customers/" + customerId;
        return restTemplate.getForObject(url, CustomerDetails.class);
    }

    // This is for local testing
//    public CustomerDetails fetchCustomerById(Long customerId) {
//        // TEMP: Dev-only stub
//        CustomerDetails customer = new CustomerDetails();
//        customer.setCustomerId(customerId);
//        customer.setName("Test User");
//        customer.setEmail("test@gmail.com");
//        customer.setPhone("9876543210");
//        customer.setKycStatus("VERIFIED");
//        customer.setCreatedAt("2022-01-01T10:00:00");
//        return customer;
//    }

    @Data
    public static class CustomerDetails {
        @JsonProperty("id")
        private Long customerId;
        private String name;
        private String email;
        private String phone;
        private String kycStatus;
        private String createdAt;
    }

}
