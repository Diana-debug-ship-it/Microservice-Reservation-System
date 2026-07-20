package diana.dev.shared.http.payment;

public enum PaymentRejectReason {
    INSUFFICIENT_FUNDS,
    CARD_EXPIRED,
    BANK_TIMEOUT,
    SBP_NOT_REGISTERED
}
