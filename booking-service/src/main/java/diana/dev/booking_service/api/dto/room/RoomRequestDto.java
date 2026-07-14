package diana.dev.booking_service.api.dto.room;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RoomRequestDto (
        @NotNull
        String number,

        @NotNull
        @Positive
        BigDecimal pricePerNight,

        @NotNull
        @Positive
        Integer maxGuests
) {
}
