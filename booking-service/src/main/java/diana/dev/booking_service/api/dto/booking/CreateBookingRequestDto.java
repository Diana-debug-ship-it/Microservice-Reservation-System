package diana.dev.booking_service.api.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CreateBookingRequestDto(
    @NotNull(message = "Room ID is required")
    Long roomId,

    @NotNull(message = "Guests number is required")
    @Positive(message = "Guests count must be greater than 0")
    Integer guests,

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    LocalDate checkInDate,

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    LocalDate checkOutDate,

    String bookingPreferences
) { }
