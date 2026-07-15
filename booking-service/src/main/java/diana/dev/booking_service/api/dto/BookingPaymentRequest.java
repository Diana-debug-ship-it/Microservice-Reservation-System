package diana.dev.booking_service.api.dto;

import diana.dev.shared.http.payment.PaymentMethod;
import jakarta.validation.constraints.NotBlank;

public record BookingPaymentRequest(
        @NotBlank(message = "Payment method is required")
        PaymentMethod paymentMethod
) {
}
