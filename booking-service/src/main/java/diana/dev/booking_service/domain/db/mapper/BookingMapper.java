package diana.dev.booking_service.domain.db.mapper;

import diana.dev.booking_service.api.dto.booking.BookingDto;
import diana.dev.booking_service.domain.db.entity.BookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface BookingMapper {

    @Mapping(target = "hotelId", source = "hotelId")
    BookingDto toBookingDto(BookingEntity entity, Long hotelId);

}
