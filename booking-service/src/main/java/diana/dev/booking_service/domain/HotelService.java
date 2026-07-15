package diana.dev.booking_service.domain;


import diana.dev.booking_service.api.dto.hotel.HotelRequestDto;
import diana.dev.booking_service.api.dto.hotel.HotelResponseDto;
import diana.dev.booking_service.domain.db.entity.HotelEntity;
import diana.dev.booking_service.domain.db.mapper.HotelMapper;
import diana.dev.booking_service.domain.db.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper mapper;

    public void validateHotelExists(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new EntityNotFoundException("Not found hotel by id=" + id);
        }
    }

    public HotelEntity getHotelReference(Long hotelId){
        validateHotelExists(hotelId);
        return hotelRepository.getReferenceById(hotelId);
    }

    public HotelResponseDto createHotel(HotelRequestDto request) {

        if (hotelRepository.existsByNameAndAddress(request.name(), request.address())) {
            throw new IllegalArgumentException("A hotel with this name already exists in this city");
        }

        HotelEntity entityToSave = HotelEntity.builder()
                .address(request.address())
                .name(request.name())
                .build();

        HotelEntity saved = hotelRepository.save(entityToSave);
        log.info("Created hotel with id={}", saved.getId());

        return mapper.toHotelDto(saved);

    }

    public HotelResponseDto getHotelById(Long id) {

        HotelEntity entity = hotelRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Not found hotel by id="+id)
        );

        log.info("Retrieved hotel with id={}", entity.getId());
        return mapper.toHotelDto(entity);
    }

    public List<HotelResponseDto> getAllHotels() {
        List<HotelEntity> hotels = hotelRepository.findAll();
        log.info("Retrieved all hotels");
        return hotels.stream().map(mapper::toHotelDto).toList();
    }
}
