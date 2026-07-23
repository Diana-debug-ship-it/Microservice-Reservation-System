package diana.dev.booking_service.domain;


import diana.dev.booking_service.api.dto.BookingDetailsSnapshot;
import diana.dev.booking_service.api.dto.BookingPaymentRequest;
import diana.dev.booking_service.api.dto.booking.BookingDto;
import diana.dev.booking_service.api.dto.booking.CreateBookingRequestDto;
import diana.dev.booking_service.external.PaymentHttpClient;
import diana.dev.booking_service.kafka.BookingConfirmedEventProducer;
import diana.dev.shared.exception.ErrorResponseDto;
import diana.dev.shared.http.booking.BookingStatus;
import diana.dev.shared.http.payment.CreatePaymentRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingProcessor {

    private final BookingService bookingService;
    private final RoomService roomService;

    private final PaymentHttpClient paymentHttpClient;

    private final ObjectMapper objectMapper;

    private final StringRedisTemplate stringRedisTemplate;
    private final static String CACHE_KEY_PREFIX = "booking:expired:";
    private final static long CACHE_TTL_MINUTES = 15;

    private final BookingConfirmedEventProducer bookingConfirmedEventProducer;


    public BookingDto createBooking(Long hotelId, CreateBookingRequestDto request) {


        BookingDetailsSnapshot details = roomService.getRoomDetailsForBooking(hotelId, request.roomId());

        var createdBooking = bookingService.createBooking(hotelId, request, details);

        cacheReservation(createdBooking);

        return createdBooking;
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
                    request.paymentMethod(),
                    request.details()
            ));

            cancelExpirationTimer(bookingId);
            return bookingService.updateBookingStatus(hotelId, bookingId, BookingStatus.CONFIRMED);

        } catch (HttpStatusCodeException e) {

            log.error("Payment failed for booking id={}", bookingId, e);
            String rawJson = e.getResponseBodyAsString();
            ErrorResponseDto paymentError = objectMapper.readValue(rawJson, ErrorResponseDto.class);
            String cleanMessage = paymentError.detailedMessage();
            throw new IllegalArgumentException(cleanMessage);

        } catch (Exception e) {

            log.error("Payment failed for booking id={}", bookingId, e);
            throw new IllegalArgumentException("Payment failed: " + e.getMessage());

        }

    }

    private void cancelExpirationTimer(Long bookingId) {
        log.info("Booking timer cancelled in Redis for booking id={}", bookingId);
        var cacheKey = CACHE_KEY_PREFIX + bookingId;
        stringRedisTemplate.delete(cacheKey);
    }

    private void cacheReservation(BookingDto bookingDto) {
        log.info("Booking timer added in Redis for booking id={}", bookingDto.id());
        var cacheKey = CACHE_KEY_PREFIX + bookingDto.id();
        stringRedisTemplate.opsForValue()
                .set(cacheKey, String.valueOf(bookingDto.hotelId()), CACHE_TTL_MINUTES, TimeUnit.MINUTES);
    }
}
