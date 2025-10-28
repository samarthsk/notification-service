package com.banking.notification.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry meterRegistry;

    public void incrementNotificationsSent(String type) {
        Counter.builder("notifications.sent")
                .tag("type", type)
                .description("Total notifications sent")
                .register(meterRegistry)
                .increment();
    }

    public void incrementNotificationsFailed(String type) {
        Counter.builder("notifications.failed")
                .tag("type", type)
                .description("Total notifications failed")
                .register(meterRegistry)
                .increment();
    }

    public Timer getNotificationLatencyTimer() {
        return Timer.builder("notification.latency")
                .description("Time taken to send notification")
                .register(meterRegistry);
    }
}
