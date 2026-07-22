package diana.dev.booking_service.api.dto;

import java.math.BigDecimal;

public record BookingDetailsSnapshot(
        String hotelName,
        String roomNumber,
        BigDecimal pricePerNight
) {
}
