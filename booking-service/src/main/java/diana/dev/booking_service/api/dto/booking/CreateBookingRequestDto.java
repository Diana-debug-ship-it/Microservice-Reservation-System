package diana.dev.booking_service.api.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CreateBookingRequestDto(
    @NotNull
    Long roomId,

    @NotNull
    @Positive
    Integer guests,

    @NotNull
    @FutureOrPresent
    LocalDate checkInDate,

    @NotNull
    @Future
    LocalDate checkOutDate,

    String bookingPreferences
) { }
