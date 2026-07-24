package diana.dev.notification_service.telegram;

import diana.dev.notification_service.domain.NotificationSender;
import diana.dev.shared.kafka.BookingConfirmedEvent;
import diana.dev.shared.kafka.NotificationChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


@Component
public class TelegramNotificationSender implements NotificationSender, LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    private final String TELEGRAM_CHAT_ID;

    public TelegramNotificationSender(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.test-chat-id}") String testChatId) {
        this.telegramClient = new OkHttpTelegramClient(botToken);
        TELEGRAM_CHAT_ID = testChatId;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.TELEGRAM;
    }

    @Override
    public void send(String text, BookingConfirmedEvent event) {
        SendMessage message = SendMessage.builder()
                .chatId(TELEGRAM_CHAT_ID)
                .text(text)
                .build();

        try {

            telegramClient.execute(message);

        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to send message via Telegram API: " + e.getMessage(), e);
        }


    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.printf(
                    "Пришло сообщение %s от %s%n",
                    update.getMessage().getText(),
                    update.getMessage().getChatId()
            );
        }
    }
}
