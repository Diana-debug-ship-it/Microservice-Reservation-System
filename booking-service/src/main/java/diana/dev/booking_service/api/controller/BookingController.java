package diana.dev.booking_service.api.controller;


import diana.dev.booking_service.api.dto.BookingPaymentRequest;
import diana.dev.booking_service.api.dto.booking.BookingDto;
import diana.dev.booking_service.api.dto.booking.CreateBookingRequestDto;
import diana.dev.booking_service.domain.BookingProcessor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/hotels/{hotelId}/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingProcessor bookingProcessor;

    @PostMapping
    public ResponseEntity<BookingDto> create(
            @PathVariable("hotelId") Long hotelId,
            @Valid @RequestBody CreateBookingRequestDto request
    ) {
        log.info("Creating booking {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingProcessor.createBooking(hotelId, request));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(
            @PathVariable("hotelId") Long hotelId,
            @PathVariable("bookingId") Long bookingId
    ) {
        log.info("Retrieving booking by id={}", bookingId);
        return ResponseEntity.status(HttpStatus.OK).body(bookingProcessor.getBookingById(hotelId, bookingId));
    }


    @GetMapping
    public ResponseEntity<List<BookingDto>> getAll(
            @PathVariable("hotelId") Long hotelId
    ) {
        log.info("Retrieving all bookings");
        return ResponseEntity.status(HttpStatus.OK).body(bookingProcessor.getAllBookings(hotelId));
    }

    @PostMapping("/{bookingId}/pay")
    public ResponseEntity<BookingDto> payBooking(
            @PathVariable("hotelId") Long hotelId,
            @PathVariable("bookingId") Long bookingId,
            @Valid @RequestBody BookingPaymentRequest request
            ) {
        log.info("Paying booking with id={}, request={}", bookingId, request);
        return ResponseEntity.status(HttpStatus.OK).body(bookingProcessor.processPayment(hotelId, bookingId, request));
    }

}
