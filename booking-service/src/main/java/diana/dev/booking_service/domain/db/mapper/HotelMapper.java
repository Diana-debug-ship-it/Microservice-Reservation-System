package diana.dev.booking_service.domain.db.mapper;


import diana.dev.booking_service.api.dto.hotel.HotelRequestDto;
import diana.dev.booking_service.api.dto.hotel.HotelResponseDto;
import diana.dev.booking_service.domain.db.entity.HotelEntity;
import org.mapstruct.*;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface HotelMapper {

    HotelEntity toEntity(HotelRequestDto requestDto);

    @AfterMapping
    default void linkHotelRoomEntities(@MappingTarget HotelEntity hotelEntity) {
        hotelEntity
                .getRooms()
                .forEach(roomEntity -> roomEntity.setHotel(hotelEntity));
    }

    HotelResponseDto toHotelDto(HotelEntity hotelEntity);

}
