package diana.dev.shared.kafka;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record BookingConfirmedEvent(
        Long bookingId,
        Long hotelId,
        String hotelName,
        String roomNumber,
        BigDecimal totalPrice,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer guestsCount
) {
}
