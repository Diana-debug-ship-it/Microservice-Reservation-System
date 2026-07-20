package diana.dev.payment_service.domain;

import diana.dev.shared.exception.PaymentRejectedException;
import diana.dev.shared.http.payment.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;

    public CreatePaymentResponseDto processPayment(CreatePaymentRequestDto request) {
        log.info("Processing payment for booking id={}", request.bookingId());

        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {}

        return switch (request.paymentDetails()) {
            case CardDetailsDto card -> handleCardPayment(request.bookingId(), request.amount(), card);
            case SbpDetailsDto sbp -> handleSbpPayment(request.bookingId(), request.amount(), sbp);
        };
    }

    private CreatePaymentResponseDto handleCardPayment(Long bookingId, BigDecimal amount, CardDetailsDto card) {

        log.info("Executing CARD payment for card number: {}", card.cardNumber());
        String cleanNumber = card.cardNumber().replaceAll("[-\\s]", "");

        if (cleanNumber.endsWith("1111")) {
            saveTransaction(bookingId, amount, PaymentMethod.CARD, PaymentStatus.PAYMENT_FAILED, null);
            throw new PaymentRejectedException(PaymentRejectReason.INSUFFICIENT_FUNDS);
        }

        if (cleanNumber.endsWith("6666")) {
            saveTransaction(bookingId, amount, PaymentMethod.CARD, PaymentStatus.PAYMENT_FAILED, null);
            throw new PaymentRejectedException(PaymentRejectReason.CARD_EXPIRED);
        }

        if (cleanNumber.endsWith("9999")) {
            throw new PaymentRejectedException(PaymentRejectReason.BANK_TIMEOUT);
        }

        String transactionId = UUID.randomUUID().toString();
        PaymentEntity payment = saveTransaction(bookingId, amount, PaymentMethod.CARD, PaymentStatus.PAYMENT_SUCCEEDED, transactionId);
        return new CreatePaymentResponseDto(payment.getId(),
                payment.getBookingId(),
                payment.getTransactionId(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getCreatedAt());
    }

    private CreatePaymentResponseDto handleSbpPayment(Long bookingId, BigDecimal amount, SbpDetailsDto sbp) {

        log.info("Executing SBP payment for phone number: {}", sbp.phoneNumber());

        if (sbp.phoneNumber().endsWith("1111")) {
            saveTransaction(bookingId, amount, PaymentMethod.SBP, PaymentStatus.PAYMENT_FAILED, null);
            throw new PaymentRejectedException(PaymentRejectReason.SBP_NOT_REGISTERED);
        }

        if (sbp.phoneNumber().endsWith("9999")) {
            throw new PaymentRejectedException(PaymentRejectReason.BANK_TIMEOUT);
        }

        String transactionId = "SBP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PaymentEntity payment = saveTransaction(bookingId, amount, PaymentMethod.SBP, PaymentStatus.PAYMENT_SUCCEEDED, transactionId);
        return new CreatePaymentResponseDto(payment.getId(),
                payment.getBookingId(),
                payment.getTransactionId(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getCreatedAt());
    }

    private PaymentEntity saveTransaction(Long bookingId, BigDecimal amount, PaymentMethod method, PaymentStatus status, String transactionId) {

        PaymentEntity entityToSave = PaymentEntity.builder()
                .bookingId(bookingId)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .paymentMethod(method)
                .status(status)
                .transactionId(transactionId)
                .build();

        return repository.save(entityToSave);
    }
}
