package diana.dev.notification_service.mail;

import diana.dev.notification_service.domain.NotificationSender;
import diana.dev.shared.kafka.BookingConfirmedEvent;
import diana.dev.shared.kafka.NotificationChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailNotificationSender implements NotificationSender {

    private final JavaMailSender javaMailSender;
    private final String testEmail;

    public MailNotificationSender(
            JavaMailSender javaMailSender,
            @Value("${notification.test-email}") String testEmail
    ) {
        this.javaMailSender = javaMailSender;
        this.testEmail = testEmail;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void send(String text, BookingConfirmedEvent event) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(testEmail);
        simpleMailMessage.setSubject("Информация о бронировании");
        simpleMailMessage.setText(text);

        javaMailSender.send(simpleMailMessage);

    }
}
