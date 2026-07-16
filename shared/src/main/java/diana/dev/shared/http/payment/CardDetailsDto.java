package diana.dev.shared.http.payment;

public final record CardDetailsDto(
        String cardNumber,
        String cvv
) implements PaymentDetailsDto {
}
