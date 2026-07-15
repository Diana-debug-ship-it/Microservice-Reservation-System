package diana.dev.booking_service.domain;


import diana.dev.booking_service.api.dto.booking.BookingDto;
import diana.dev.booking_service.api.dto.booking.CreateBookingRequestDto;
import diana.dev.booking_service.api.dto.room.RoomResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingProcessor {

    private final BookingService bookingService;
    private final HotelService hotelService;
    private final RoomService roomService;


    public BookingDto createBooking(Long hotelId, CreateBookingRequestDto request) {

        RoomResponseDto room = roomService.getRoomById(hotelId, request.roomId());

        return bookingService.createBooking(request, room.pricePerNight());

    }

    public BookingDto getBookingById(Long id, Long bookingId) {
        return bookingService.getBookingById(id, bookingId);
    }

    public List<BookingDto> getAllBookings(Long hotelId) {
        return bookingService.getAllBookings(hotelId);
    }
}
