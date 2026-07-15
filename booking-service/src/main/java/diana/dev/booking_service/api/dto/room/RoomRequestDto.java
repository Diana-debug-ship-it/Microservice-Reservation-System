package diana.dev.booking_service.api.dto.room;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RoomRequestDto (
        @NotNull(message = "Room number is required")
        String number,

        @NotNull(message = "Price per night is required")
        @Positive(message = "Price per night must be greater than 0")
        BigDecimal pricePerNight,

        @NotNull(message = "Max guests is required")
        @Positive(message = "Max guests must be greater than 0")
        Integer maxGuests
) {
}
