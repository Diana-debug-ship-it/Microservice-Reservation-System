package diana.dev.booking_service.hotel;

import diana.dev.booking_service.api.dto.hotel.HotelRequestDto;
import diana.dev.booking_service.api.dto.hotel.HotelResponseDto;
import diana.dev.booking_service.domain.HotelService;
import diana.dev.booking_service.domain.db.entity.HotelEntity;
import diana.dev.booking_service.domain.db.mapper.HotelMapper;
import diana.dev.booking_service.domain.db.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {

    @InjectMocks
    private HotelService hotelService;

    @Mock
    private HotelRepository hotelRepository;

    @Spy
    private final HotelMapper mapper = Mappers.getMapper(HotelMapper.class);

    @Test
    void validateHotelExists_ShouldDoNothing_WhenHotelExists() {

        Long hotelId = 3L;

        when(hotelRepository.existsById(hotelId)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> hotelService.validateHotelExists(hotelId));

    }

    @Test
    void validateHotelExists_ShouldThrowEntityNotFoundException_WhenHotelDoesNotExist() {

        Long hotelId = 3L;

        when(hotelRepository.existsById(hotelId)).thenReturn(false);

        var result = Assertions.assertThrows(EntityNotFoundException.class, () -> hotelService.validateHotelExists(hotelId));
        Assertions.assertEquals("Not found hotel by id=3", result.getMessage());

    }


    @Test
    void getHotelById_ShouldReturnHotelResponseDto_WhenHotelExists() {

        Long hotelId = 3L;

        HotelEntity hotelEntity = new HotelEntity(hotelId, "hotel", "address", List.of());
        HotelResponseDto hotelResponseDto = new HotelResponseDto(hotelId, "hotel", "address", List.of());
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotelEntity));

        HotelResponseDto response = hotelService.getHotelById(hotelId);

        Assertions.assertEquals(hotelId, response.id());

    }

    @Test
    void getHotelById_ShouldThrowNotFoundEntityException_WhenHotelDoesNotExist() {

        Long hotelId = 3L;

        when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> hotelService.getHotelById(hotelId));

    }

    @Test
    void getAllHotels_ShouldReturnListOfHotels_WhenHotelsExist() {

        HotelEntity hotelEntity1 = new HotelEntity(1L, "hotel1", "address1", List.of());
        HotelEntity hotelEntity2 = new HotelEntity(2L, "hotel2", "address2", List.of());
        HotelEntity hotelEntity3 = new HotelEntity(3L, "hotel3", "address3", List.of());

        when(hotelRepository.findAll()).thenReturn(List.of(hotelEntity1, hotelEntity2, hotelEntity3));

        List<HotelResponseDto> hotels = hotelService.getAllHotels();

        Assertions.assertEquals(3, hotels.size());
        Assertions.assertEquals(1L, hotels.get(0).id());
        Assertions.assertEquals(2L, hotels.get(1).id());
        Assertions.assertEquals(3L, hotels.get(2).id());

    }

    @Test
    void getAllHotels_ShouldReturnEmptyList_WhenNoHotelExists() {

        when(hotelRepository.findAll()).thenReturn(List.of());

        List<HotelResponseDto> hotels = hotelService.getAllHotels();

        Assertions.assertEquals(0, hotels.size());

    }

    @Test
    void createHotel_ShouldReturnHotelResponse_WhenHotelIsValid() {

        HotelRequestDto request = new HotelRequestDto("hotel", "address");
        HotelEntity savedHotel = new HotelEntity(1L, "hotel", "address", List.of());

        when(hotelRepository.save(any(HotelEntity.class))).thenReturn(savedHotel);
        when(hotelRepository.existsByNameAndAddress(request.name(), request.address())).thenReturn(false);

        HotelResponseDto response = hotelService.createHotel(request);

        Assertions.assertNotNull(response.id());
        Assertions.assertEquals(1L, response.id());

    }

    @Test
    void createHotel_ShouldThrowIllegalArgumentException_WhenHotelAlreadyExists() {

        HotelRequestDto request = new HotelRequestDto("hotel", "address");
        HotelEntity hotelToSave = new HotelEntity(null, "hotel", "address", List.of());

        when(hotelRepository.existsByNameAndAddress(hotelToSave.getName(), hotelToSave.getAddress())).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> hotelService.createHotel(request));
        verify(hotelRepository, never()).save(any(HotelEntity.class));
    }

    @Test
    void getHotelReference_ShouldReturnHotelEntity_WhenHotelExists() {
        Long hotelId = 3L;

        HotelEntity hotelEntity = new HotelEntity(hotelId, "hotel", "address", List.of());

        when(hotelRepository.existsById(hotelId)).thenReturn(true);
        when(hotelRepository.getReferenceById(hotelId)).thenReturn(hotelEntity);

        HotelEntity entity = hotelService.getHotelReference(hotelId);

        Assertions.assertEquals(hotelId, entity.getId());
    }

    @Test
    void getHotelReference_ShouldThrowNotFoundEntityException_WhenHotelDoesNotExist() {
        Long hotelId = 3L;

        when(hotelRepository.existsById(hotelId)).thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class, () -> hotelService.getHotelReference(hotelId));
    }
}
