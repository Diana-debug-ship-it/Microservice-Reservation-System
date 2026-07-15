package diana.dev.booking_service.domain;


import diana.dev.booking_service.api.dto.BookingPaymentRequest;
import diana.dev.booking_service.api.dto.booking.BookingDto;
import diana.dev.booking_service.api.dto.booking.CreateBookingRequestDto;
import diana.dev.booking_service.api.dto.room.RoomResponseDto;
import diana.dev.booking_service.external.PaymentHttpClient;
import diana.dev.shared.http.booking.BookingStatus;
import diana.dev.shared.http.payment.CreatePaymentRequestDto;
import diana.dev.shared.http.payment.PaymentStatus;
import jakarta.validation.Valid;
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
    private final PaymentHttpClient paymentHttpClient;


    public BookingDto createBooking(Long hotelId, CreateBookingRequestDto request) {

        RoomResponseDto room = roomService.getRoomById(hotelId, request.roomId());

        return bookingService.createBooking(hotelId, request, room.pricePerNight());

    }

    public BookingDto getBookingById(Long id, Long bookingId) {
        return bookingService.getBookingById(id, bookingId);
    }

    public List<BookingDto> getAllBookings(Long hotelId) {
        return bookingService.getAllBookings(hotelId);
    }

    public BookingDto processPayment(Long hotelId, Long bookingId, BookingPaymentRequest request) {

        var booking = bookingService.getBookingById(hotelId, bookingId);

        if (!booking.status().equals(BookingStatus.PENDING_PAYMENT)) {
            throw new IllegalStateException("Booking must be in status PENDING_PAYMENT");
        }

        try {
            var response = paymentHttpClient.createPayment(new CreatePaymentRequestDto(
                    bookingId,
                    booking.totalPrice(),
                    request.paymentMethod()
            ));

            var status = response.status().equals(PaymentStatus.PAYMENT_SUCCEEDED)
                    ? BookingStatus.CONFIRMED
                    : BookingStatus.CANCELLED;

            return bookingService.updateBookingStatus(hotelId, bookingId, status);
        } catch (Exception e) {
            log.error("Payment failed for booking id={}. Cancelling booking.", bookingId, e);
            throw new IllegalArgumentException("Payment failed: " + e.getMessage());
        }

    }
}
