package diana.dev.booking_service.api.dto.hotel;

import jakarta.validation.constraints.NotNull;

public record HotelRequestDto(
        @NotNull(message = "Hotel name is required")
        String name,

        @NotNull(message = "Hotel address is required")
        String address
) {
}
