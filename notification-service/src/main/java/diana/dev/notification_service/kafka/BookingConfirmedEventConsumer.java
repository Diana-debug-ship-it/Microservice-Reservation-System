package diana.dev.notification_service.kafka;


import diana.dev.notification_service.domain.NotificationChannel;
import diana.dev.notification_service.domain.NotificationService;
import diana.dev.shared.kafka.BookingConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingConfirmedEventConsumer {

    private final NotificationService notificationService;


    @KafkaListener(
            topics = "${booking-confirmed-topic}",
            groupId = "booking-notification-group",
            containerFactory = "bookingConfirmedEventListenerFactory"
    )
    public void listen(BookingConfirmedEvent event) {
        log.info("Received booking confirmed event: {}", event);
        notificationService.processBookingConfirmedEvent(event, NotificationChannel.LOG);
    }

}
