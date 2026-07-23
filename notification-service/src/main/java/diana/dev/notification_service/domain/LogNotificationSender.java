package diana.dev.notification_service.domain;

import diana.dev.shared.kafka.BookingConfirmedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogNotificationSender implements NotificationSender{
    @Override
    public diana.dev.shared.kafka.NotificationChannel getChannel() {
        return diana.dev.shared.kafka.NotificationChannel.LOG;
    }

    @Override
    public void send(String text, BookingConfirmedEvent event) {
        log.info("Sending notification via LOG: {}", text);
    }
}
