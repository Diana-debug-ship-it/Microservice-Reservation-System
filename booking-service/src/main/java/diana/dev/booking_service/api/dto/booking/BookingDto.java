package diana.dev.booking_service.api.dto.booking;

import diana.dev.shared.http.booking.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingDto(
        Long id,
        Long roomId,
        Long hotelId,
        Integer guests,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        BookingStatus status,
        BigDecimal totalPrice,
        String bookingPreferences
) {
}
