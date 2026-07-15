package diana.dev.shared.http.payment;

import java.math.BigDecimal;

public record CreatePaymentRequestDto(
        Long bookingId,
        BigDecimal amount,
        PaymentMethod paymentMethod
) {
}
