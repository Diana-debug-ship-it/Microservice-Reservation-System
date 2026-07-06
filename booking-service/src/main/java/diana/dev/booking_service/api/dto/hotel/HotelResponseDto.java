package diana.dev.booking_service.api.dto.hotel;

import diana.dev.booking_service.api.dto.room.RoomResponseDto;

import java.util.List;

public record HotelResponseDto(
        Long id,
        String name,
        String address,
        List<RoomResponseDto> rooms
) {
}
