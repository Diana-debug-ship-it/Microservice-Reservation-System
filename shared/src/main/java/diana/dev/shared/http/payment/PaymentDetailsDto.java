package diana.dev.shared.http.payment;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CardDetailsDto.class, name = "CARD"),
        @JsonSubTypes.Type(value = SbpDetailsDto.class, name = "SBP")
})
public sealed interface PaymentDetailsDto permits CardDetailsDto, SbpDetailsDto {
}
