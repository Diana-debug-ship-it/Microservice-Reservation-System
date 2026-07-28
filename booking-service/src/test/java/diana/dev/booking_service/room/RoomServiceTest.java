package diana.dev.booking_service.room;

import diana.dev.booking_service.api.dto.BookingDetailsSnapshot;
import diana.dev.booking_service.api.dto.room.RoomRequestDto;
import diana.dev.booking_service.api.dto.room.RoomResponseDto;
import diana.dev.booking_service.domain.HotelService;
import diana.dev.booking_service.domain.RoomService;
import diana.dev.booking_service.domain.db.entity.HotelEntity;
import diana.dev.booking_service.domain.db.entity.RoomEntity;
import diana.dev.booking_service.domain.db.mapper.RoomMapper;
import diana.dev.booking_service.domain.db.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @InjectMocks
    private RoomService roomService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HotelService hotelService;

    @Spy
    private final RoomMapper mapper = Mappers.getMapper(RoomMapper.class);

    @Test
    void getRoomById_ShouldReturnRoomResponseDto_WhenRoomExists() {

        Long hotelId = 5L;
        Long id = 3L;

        HotelEntity hotel = new HotelEntity(hotelId, "hotel", "address", List.of());
        RoomEntity roomEntity = new RoomEntity(id, "101", BigDecimal.valueOf(1000), hotel, 5);

        when(roomRepository.findByIdAndHotelId(id, hotelId)).thenReturn(Optional.of(roomEntity));

        RoomResponseDto room = roomService.getRoomById(hotelId, id);

        Assertions.assertEquals(id, room.id());
        Assertions.assertEquals(hotelId, room.hotelId());

    }

    @Test
    void getRoomById_ShouldThrowNotFoundEntityException_WhenRoomDoesNotExist() {

        Long hotelId = 5L;
        Long id = 3L;

        when(roomRepository.findByIdAndHotelId(id, hotelId)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> roomService.getRoomById(hotelId, id));

    }

    @Test
    void getAllRooms_ShouldReturnListOfRooms_WhenRoomsExist() {

        Long hotelId = 5L;

        HotelEntity hotel = new HotelEntity(hotelId, "hotel", "address", List.of());
        RoomEntity room1 = new RoomEntity(1L, "101", BigDecimal.valueOf(1000), hotel, 2);
        RoomEntity room2 = new RoomEntity(2L, "102", BigDecimal.valueOf(2000), hotel, 3);
        RoomEntity room3 = new RoomEntity(3L, "103", BigDecimal.valueOf(3000), hotel, 4);

        when(roomRepository.findByHotelId(hotelId)).thenReturn(List.of(room1, room2, room3));

        List<RoomResponseDto> rooms = roomService.getAllRooms(hotelId);

        Assertions.assertEquals(3, rooms.size());
        Assertions.assertEquals(1L, rooms.get(0).id());
        Assertions.assertEquals(2L, rooms.get(1).id());
        Assertions.assertEquals(3L, rooms.get(2).id());

    }

    @Test
    void getAllRooms_ShouldReturnEmptyList_WhenNoRoomsExist() {

        Long hotelId = 5L;

        when(roomRepository.findByHotelId(hotelId)).thenReturn(List.of());

        List<RoomResponseDto> rooms = roomService.getAllRooms(hotelId);

        Assertions.assertEquals(0, rooms.size());

    }

    @Test
    void createRoom_ShouldReturnRoomResponse_WhenRoomIsValid() {

        Long hotelId = 3L;
        Long id = 5L;

        RoomRequestDto request = new RoomRequestDto("101", BigDecimal.valueOf(1000), 2);
        HotelEntity hotel = new HotelEntity(hotelId, "hotel", "address", List.of());
        RoomEntity savedRoom = new RoomEntity(id, "101", BigDecimal.valueOf(1000), hotel, 2);

        when(hotelService.getHotelReference(hotelId)).thenReturn(hotel);
        when(roomRepository.save(any())).thenReturn(savedRoom);

        RoomResponseDto response = roomService.createRoom(hotelId, request);

        Assertions.assertNotNull(response.id());
        Assertions.assertEquals(id, response.id());
    }

    @Test
    void getRoomDetailsForBooking_ShouldReturnBookingDetailsSnapshot_WhenRoomExists() {

        Long hotelId = 3L;
        Long id = 5L;

        BookingDetailsSnapshot snapshot = new BookingDetailsSnapshot("hotel", "101", BigDecimal.valueOf(1000));

        when(roomRepository.getSnapshotForBooking(hotelId, id)).thenReturn(Optional.of(snapshot));

        var returned = roomService.getRoomDetailsForBooking(hotelId, id);

        Assertions.assertEquals("hotel", returned.hotelName());
        Assertions.assertEquals("101", returned.roomNumber());
        Assertions.assertEquals(BigDecimal.valueOf(1000), returned.pricePerNight());

    }

    @Test
    void getRoomDetailsForBooking_ShouldThrowEntityNotFoundException_WhenRoomDoesNotExist() {

        Long hotelId = 3L;
        Long id = 5L;

        when(roomRepository.getSnapshotForBooking(hotelId, id)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> roomService.getRoomDetailsForBooking(hotelId, id));

    }

    @Test
    void getRoomPrice_ShouldReturnPrice_WhenRoomExists() {

        Long hotelId = 3L;
        Long id = 5L;

        HotelEntity hotel = new HotelEntity(hotelId, "hotel", "address", List.of());
        RoomEntity room = new RoomEntity(id, "101", BigDecimal.valueOf(1000), hotel, 2);

        when(roomRepository.findByIdAndHotelId(id, hotelId)).thenReturn(Optional.of(room));

        var price = roomService.getRoomPrice(hotelId, id);

        Assertions.assertEquals(BigDecimal.valueOf(1000), price);
    }

    @Test
    void getRoomPrice_ShouldThrowEntityNotFoundException_WhenRoomDoesNotExist() {

        Long hotelId = 3L;
        Long id = 5L;

        when(roomRepository.findByIdAndHotelId(id, hotelId)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> roomService.getRoomPrice(hotelId, id));

    }

}
