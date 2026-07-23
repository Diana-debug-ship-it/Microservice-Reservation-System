package diana.dev.notification_service.domain;

import diana.dev.shared.kafka.BookingConfirmedEvent;

public interface NotificationSender {

    NotificationChannel getChannel();

    void send(String text, BookingConfirmedEvent event);

}
