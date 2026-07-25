package diana.dev.booking_service.api.dto.hotel;

import jakarta.validation.constraints.NotBlank;

public record HotelRequestDto(
        @NotBlank(message = "Hotel name is required")
        String name,

        @NotBlank(message = "Hotel address is required")
        String address
) {
}
