package app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import app.service.TelegramBot;

@Component
public class BotInitializer {
    @Autowired
    TelegramBot telegramBot;
    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(telegramBot);
            System.out.println("Бот запущен");
        } catch (TelegramApiException e) {
            System.out.println("Ошибка при запуске бота: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
