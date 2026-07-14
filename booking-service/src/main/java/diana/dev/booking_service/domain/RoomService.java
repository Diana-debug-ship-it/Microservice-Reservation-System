package diana.dev.booking_service.domain;

import diana.dev.booking_service.api.dto.room.RoomRequestDto;
import diana.dev.booking_service.api.dto.room.RoomResponseDto;
import diana.dev.booking_service.domain.db.entity.HotelEntity;
import diana.dev.booking_service.domain.db.entity.RoomEntity;
import diana.dev.booking_service.domain.db.mapper.RoomMapper;
import diana.dev.booking_service.domain.db.repository.HotelRepository;
import diana.dev.booking_service.domain.db.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final HotelService hotelService;
    private final RoomRepository roomRepository;
    private final RoomMapper mapper;

    public RoomResponseDto createRoom(Long hotelId, RoomRequestDto request) {

        hotelService.validateHotelExists(hotelId);

        RoomEntity entityToSave = mapper.toEntity(request);

        RoomEntity saved = roomRepository.save(entityToSave);
        log.info("Created room with id={}", saved.getId());

        return mapper.toRoomDto(saved);
    }

    public RoomResponseDto getRoomById(Long hotelId, Long roomId) {

        RoomEntity roomEntity = roomRepository.findByIdAndHotelId(roomId, hotelId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Not found room id=" + roomId + " in hotel id=" + hotelId)
                );

        log.info("Retrieved room by id={} in the hotel id={}", roomId, hotelId);

        return mapper.toRoomDto(roomEntity);
    }

    public List<RoomResponseDto> getAllRooms(Long hotelId) {

        hotelService.validateHotelExists(hotelId);

        List<RoomEntity> roomEntityList = roomRepository.findByHotelId(hotelId);

        log.info("Retrieved all rooms in the hotel id={}", hotelId);

        return roomEntityList.stream().map(mapper::toRoomDto).toList();
    }
}
