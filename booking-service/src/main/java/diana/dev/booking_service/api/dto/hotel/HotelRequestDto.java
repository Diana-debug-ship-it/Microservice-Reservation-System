package diana.dev.booking_service.api.dto.hotel;

import jakarta.validation.constraints.NotNull;

public record HotelRequestDto(
        @NotNull
        String name,

        @NotNull
        String address
) {
}
