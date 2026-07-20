package diana.dev.shared.http.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreatePaymentResponseDto(
        Long paymentId,
        Long bookingId,
        String transactionId,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        BigDecimal amount,
        LocalDateTime createdAt
) {
}
