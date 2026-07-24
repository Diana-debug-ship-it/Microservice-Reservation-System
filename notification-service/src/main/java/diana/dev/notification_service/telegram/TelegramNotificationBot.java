package diana.dev.notification_service.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;


@Component
@RequiredArgsConstructor
public class TelegramNotificationBot implements SpringLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final TelegramNotificationSender sender;

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return sender;
    }
}
