package diana.dev.shared.exception;

import diana.dev.shared.http.payment.PaymentRejectReason;

public class PaymentRejectedException extends RuntimeException {
    private final PaymentRejectReason reason;

    public PaymentRejectedException(PaymentRejectReason reason) {
        super(reason.name());
        this.reason = reason;
    }
}
