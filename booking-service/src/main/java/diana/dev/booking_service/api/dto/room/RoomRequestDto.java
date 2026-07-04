package diana.dev.booking_service.api.dto.room;

import diana.dev.booking_service.domain.db.HotelEntity;

import java.math.BigDecimal;

public record RoomRequestDto (
        String number,
        BigDecimal pricePerNight,
        Integer maxGuests
) {
}
