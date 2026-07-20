package diana.dev.payment_service.api;


import diana.dev.payment_service.domain.PaymentService;
import diana.dev.shared.http.payment.CreatePaymentRequestDto;
import diana.dev.shared.http.payment.CreatePaymentResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping
    public ResponseEntity<CreatePaymentResponseDto> createPayment(
            @RequestBody CreatePaymentRequestDto request
    ) {
        log.info("Received request: payment request {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(request));
    }

}
