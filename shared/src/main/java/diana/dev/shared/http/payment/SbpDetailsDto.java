package diana.dev.shared.http.payment;

public final record SbpDetailsDto(
        String phoneNumber
) implements PaymentDetailsDto {
}
