package diana.dev.shared.http.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final record SbpDetailsDto(

        @NotBlank(message = "Phone number is required")
        @Size(min = 11, max = 12, message = "Phone number is invalid")
        String phoneNumber
) implements PaymentDetailsDto {
}
