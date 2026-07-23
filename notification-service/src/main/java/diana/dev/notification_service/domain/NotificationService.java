package diana.dev.notification_service.domain;

import diana.dev.notification_service.domain.db.NotificationEntity;
import diana.dev.notification_service.domain.db.NotificationRepository;
import diana.dev.shared.kafka.BookingConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final List<NotificationSender> senders;

    public void processBookingConfirmedEvent(
        BookingConfirmedEvent event, NotificationChannel channel
    ) {
        log.info("Processing Booking Confirmed Event {} for channel {}", event, channel);

        String text = buildNotificationText(event);

        NotificationEntity notificationEntity = new NotificationEntity(
                null,
                event.bookingId(),
                channel,
                NotificationStatus.CREATED,
                text,
                LocalDateTime.now(),
                null
        );

        var saved = notificationRepository.save(notificationEntity);


        var targetSender = senders.stream()
                .filter(sender -> sender.getChannel() == channel)
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("No sender found for channel: " + channel)
                );

        try {

            targetSender.send(text, event);

            saved.setStatus(NotificationStatus.SENT);
            saved.setSentAt(LocalDateTime.now());

        } catch (Exception e) {

            log.error("Failed to send notification via channel {} for booking id={}", channel, event.bookingId(), e);
            saved.setStatus(NotificationStatus.FAILED);

        }

        notificationRepository.save(saved);

    }

    private String buildNotificationText(
        BookingConfirmedEvent event
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        return String.format(
                "Уважаемый клиент! Ваше бронирование №%d в отеле '%s' успешно подтверждено и оплачено. " +
                        "Номер комнаты: %s. Даты проживания: %s — %s. Количество гостей: %d. " +
                        "Итоговая стоимость: %.2f руб. Ждем вас!",
                event.bookingId(),
                event.hotelName(),
                event.roomNumber(),
                event.checkInDate().format(formatter),
                event.checkOutDate().format(formatter),
                event.guestsCount(),
                event.totalPrice()
        );
    }

}
