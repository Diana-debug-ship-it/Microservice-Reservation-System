package diana.dev.booking_service.domain;


import diana.dev.booking_service.api.dto.BookingDetailsSnapshot;
import diana.dev.booking_service.api.dto.booking.BookingDto;
import diana.dev.booking_service.api.dto.booking.CreateBookingRequestDto;
import diana.dev.booking_service.domain.db.entity.BookingEntity;
import diana.dev.booking_service.domain.db.mapper.BookingMapper;
import diana.dev.booking_service.domain.db.repository.BookingRepository;
import diana.dev.shared.http.booking.BookingStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;

    public BookingDto createBooking(Long hotelId, CreateBookingRequestDto request, BookingDetailsSnapshot details) {

        LocalDate checkIn = request.checkInDate();
        LocalDate checkOut = request.checkOutDate();
        Long roomId = request.roomId();

        if (repository.existsOverlappingBooking(roomId, checkIn, checkOut)) {
            throw new IllegalArgumentException("The room " + roomId + " is occupied on these dates: " +
                    checkIn + " - " + checkOut);
        }

        BigDecimal totalPrice = calculateTotal(checkIn, checkOut, details.pricePerNight());

        BookingEntity entityToSave = new BookingEntity(
                null,
                roomId,
                details.roomNumber(),
                hotelId,
                details.hotelName(),
                request.guests(),
                BookingStatus.PENDING_PAYMENT,
                checkIn,
                checkOut,
                totalPrice,
                request.bookingPreferences()
        );

        BookingEntity savedEntity = repository.save(entityToSave);
        log.info("Created booking with id={}", savedEntity.getId());

        return mapper.toBookingDto(savedEntity, hotelId);
    }

    public BookingDto getBookingById(Long hotelId, Long bookingId) {

        BookingEntity entity = repository.findByIdAndHotelId(bookingId, hotelId).orElseThrow(
                () -> new EntityNotFoundException("Not found booking by id " + bookingId + " in hotel " + hotelId)
        );

        log.info("Retrieved booking by id={} for hotel id={}", bookingId, hotelId);
        return mapper.toBookingDto(entity, hotelId);
    }

    public List<BookingDto> getAllBookings(Long hotelId) {

        List<BookingEntity> entities = repository.findAllByHotelId(hotelId);
        log.info("Retrieved all bookings in hotel by id={}", hotelId);
        return entities.stream().map(entity -> mapper.toBookingDto(entity, hotelId)).toList();

    }

    private BigDecimal calculateTotal(LocalDate checkIn, LocalDate checkOut, BigDecimal pricePerNight) {
        long days = ChronoUnit.DAYS.between(checkIn, checkOut);

        if (days<=0) {
            throw new IllegalArgumentException("The departure date must be later than the arrival date");
        }

        return pricePerNight.multiply(BigDecimal.valueOf(days));
    }

    public BookingDto updateBookingStatus(Long hotelId, Long bookingId, BookingStatus status) {

        BookingEntity entity = repository.findByIdAndHotelId(bookingId, hotelId).orElseThrow(
                () -> new EntityNotFoundException("Not found booking by id " + bookingId + " in hotel " + hotelId)
        );
        log.info("Updating booking by id={} for hotel id={}", bookingId, hotelId);

        entity.setStatus(status);
        var updated = repository.save(entity);

        return mapper.toBookingDto(updated, hotelId);
    }
}
