package diana.dev.booking_service.redis;

import diana.dev.booking_service.domain.BookingService;
import diana.dev.booking_service.domain.db.repository.BookingRepository;
import diana.dev.shared.http.booking.BookingStatus;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;



@Component
public class BookingKeyExpirationListener extends KeyExpirationEventMessageListener {

    private final BookingRepository repository;

    public BookingKeyExpirationListener(RedisMessageListenerContainer listenerContainer, BookingService service, BookingRepository repository) {
        super(listenerContainer);
        this.repository = repository;
    }

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        String msg = message.toString();
        if (msg.startsWith("booking:expired:")) {
            Long bookingId = Long.valueOf(msg.substring(16));

            repository.findById(bookingId).ifPresent(booking -> {
                if (booking.getStatus().equals(BookingStatus.PENDING_PAYMENT)) {
                    booking.setStatus(BookingStatus.CANCELLED);
                    repository.save(booking);
                }
            });

        }
    }
}
