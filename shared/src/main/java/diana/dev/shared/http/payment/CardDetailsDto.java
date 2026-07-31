package diana.dev.shared.http.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final record CardDetailsDto(

        @NotBlank
        @Size(min = 16, max = 16, message = "Card number must be exactly 16 digits")
        String cardNumber,

        @NotBlank
        @Size(min = 3, max = 3, message = "CVV must be exactly 3 digits")
        String cvv
) implements PaymentDetailsDto {
}
