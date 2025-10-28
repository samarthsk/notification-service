package com.banking.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Queue names
    public static final String TRANSACTION_NOTIFICATION_QUEUE = "transaction.notification.queue";
    public static final String ACCOUNT_STATUS_QUEUE = "account.status.queue";

    // Exchange names
    public static final String TRANSACTION_EXCHANGE = "transaction.exchange";
    public static final String ACCOUNT_EXCHANGE = "account.exchange";

    // Routing keys
    public static final String TRANSACTION_ROUTING_KEY = "transaction.notification";
    public static final String ACCOUNT_STATUS_ROUTING_KEY = "account.status.change";

    // Queues
    @Bean
    public Queue transactionNotificationQueue() {
        return new Queue(TRANSACTION_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Queue accountStatusQueue() {
        return new Queue(ACCOUNT_STATUS_QUEUE, true);
    }

    // Exchanges
    @Bean
    public TopicExchange transactionExchange() {
        return new TopicExchange(TRANSACTION_EXCHANGE);
    }

    @Bean
    public TopicExchange accountExchange() {
        return new TopicExchange(ACCOUNT_EXCHANGE);
    }

    // Bindings
    @Bean
    public Binding transactionNotificationBinding() {
        return BindingBuilder
                .bind(transactionNotificationQueue())
                .to(transactionExchange())
                .with(TRANSACTION_ROUTING_KEY);
    }

    @Bean
    public Binding accountStatusBinding() {
        return BindingBuilder
                .bind(accountStatusQueue())
                .to(accountExchange())
                .with(ACCOUNT_STATUS_ROUTING_KEY);
    }

    // Message converter for JSON
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
