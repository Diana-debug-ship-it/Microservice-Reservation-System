package diana.dev.booking_service.kafka;

import diana.dev.booking_service.api.dto.booking.BookingDto;
import diana.dev.booking_service.domain.db.entity.BookingEntity;
import diana.dev.booking_service.domain.db.entity.HotelEntity;
import diana.dev.shared.kafka.BookingConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingConfirmedEventProducer {


    private final KafkaTemplate<Long, BookingConfirmedEvent> kafkaTemplate;

    @Value("${booking-confirmed-topic}")
    private String bookingConfirmedTopic;

    public void sendBookingConfirmedEvent(
            BookingDto bookingDto
    ) {
        kafkaTemplate.send(
                bookingConfirmedTopic,
                bookingDto.id(),
                BookingConfirmedEvent.builder()
                        .bookingId(bookingDto.id())
                        .checkInDate(bookingDto.checkInDate())
                        .checkOutDate(bookingDto.checkOutDate())
                        .guestsCount(bookingDto.guests())
                        .hotelId(bookingDto.hotelId())
                        .hotelName(bookingDto.hotelName())
                        .roomNumber(bookingDto.roomNumber())
                        .totalPrice(bookingDto.totalPrice())
                        .build()

        ).thenAccept(result -> {
            log.info("Booking confirmed event sent with id={}", bookingDto.id());
        });
    }

}
