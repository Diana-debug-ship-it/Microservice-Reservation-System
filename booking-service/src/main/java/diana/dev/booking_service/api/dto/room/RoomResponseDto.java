package diana.dev.booking_service.api.dto.room;

import java.math.BigDecimal;

public record RoomResponseDto(
        Long id,
        Long hotelId,
        String number,
        BigDecimal pricePerNight,
        Integer maxGuests
) {
}
