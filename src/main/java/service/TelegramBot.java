//package service;
//
//import client.Client;
//import config.BotConfig;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import java.io.IOException;
//
//@Component
//
//public class TelegramBot extends TelegramLongPollingBot {
//    private BotConfig config;
//    private Client client;
//    public TelegramBot(BotConfig config) {
//        this.config = config;
//        try {
//            this.client = new Client();
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }
//        client.startConnection();
//    }
//    @Override
//    public void onUpdateReceived(Update update) {
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            String messageText = update.getMessage().getText();
//            long chatId = update.getMessage().getChatId();
//            try {
//                SendMessage message = new SendMessage();
//                message.setChatId(String.valueOf(chatId));
//                switch (messageText){
//                    case "/start":
//                        message.setText("Добро пожаловать в бота Space Marines!");
//                        break;
//                    default:
//                        message.setText("Вы написали: " + messageText);
//                        break;
//                }
//                execute(message);
//            }
//            catch (TelegramApiException e){
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public String getBotUsername() {
//        return config.getBotName();
//    }
//    @Override
//    public String getBotToken() {
//        return config.getBotToken();
//    }
//}
