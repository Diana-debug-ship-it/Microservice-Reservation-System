package diana.dev.booking_service.domain.db.mapper;


import diana.dev.booking_service.api.dto.room.RoomRequestDto;
import diana.dev.booking_service.api.dto.room.RoomResponseDto;
import diana.dev.booking_service.domain.db.entity.RoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomMapper {

    RoomEntity toEntity(RoomRequestDto requestDto);

    @Mapping(target = "hotelId", source = "entity.hotel.id")
    RoomResponseDto toRoomDto(RoomEntity entity);

}
