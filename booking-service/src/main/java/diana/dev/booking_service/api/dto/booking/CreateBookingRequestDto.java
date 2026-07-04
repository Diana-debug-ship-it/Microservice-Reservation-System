package diana.dev.booking_service.api.dto.booking;

import java.time.LocalDate;

public record CreateBookingRequestDto(
    Long roomId,
    Integer guests,
    LocalDate checkInDate,
    LocalDate checkOutDate,
    String bookingPreferences
) { }
