package diana.dev.shared.http.payment;

import java.math.BigDecimal;

public record CreatePaymentResponseDto(
        Long paymentId,
        PaymentStatus status,
        Long bookingId,
        PaymentMethod paymentMethod,
        BigDecimal amount
) {
}
