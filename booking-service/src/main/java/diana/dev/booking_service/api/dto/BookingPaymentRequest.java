package diana.dev.booking_service.api.dto;

import diana.dev.shared.http.payment.PaymentDetailsDto;
import diana.dev.shared.http.payment.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookingPaymentRequest(
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @NotNull(message = "Payment details are required")
        PaymentDetailsDto details
) {
}
