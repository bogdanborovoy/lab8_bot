package app.service;




import app.config.BotConfig;
import commands.AddCommands;
import database.DatabaseManager;
import helpers.CollectionManager;
import helpers.Invoker;
import models.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;
    DatabaseManager databaseManager;
    volatile Map<Long, TreeSet<SpaceMarine>> allSpaceMarines;
    TreeSet<SpaceMarine> currentUserSpaceMarines;
    CollectionManager cm;
    Invoker invoker;

    public TelegramBot(BotConfig config) {
        this.config = config;
        databaseManager = new DatabaseManager();
        allSpaceMarines = Collections.synchronizedMap(new HashMap<>());
        cm = new CollectionManager();
        invoker = new Invoker(cm);
        startConsoleInputHandler();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String[] args = messageText.split(" ");
            String line = args[0].toLowerCase();
            if (args.length > 1) {
                args = Arrays.copyOfRange(args, 1, args.length);
            }
            long chatId = update.getMessage().getChatId();
            currentUserSpaceMarines = allSpaceMarines.getOrDefault(chatId, new TreeSet<>(new CollectionManager.IDComparator()));
            cm.spaceMarines = currentUserSpaceMarines;


            if (AddCommands.getNames().contains(line)){
                var spaceMarine = addCommandReceived(chatId, args);
                var command = invoker.getCommands().get(line);
                if (spaceMarine != null) {
                    command.passSpaceMarine(spaceMarine);
                    invoker.runCommand(command);
                    sendMessage(chatId, "Команда выполнена успешно");
                    currentUserSpaceMarines = cm.spaceMarines;
                }
            }
            else {
                switch (line) {
                    case "/start":
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case "change_language":
                    case "show":
                        sendMessage(chatId, cm.show(cm.spaceMarines));
                        break;
                    case "help":
                        sendMessage(chatId, cm.help(invoker));
                        break;
                    case "remove_by_id":
                        invoker.runCommand(invoker.getCommands().get("remove_by_id"), args[0]);
                        break;
                    case "clear":
                        invoker.runCommand(invoker.getCommands().get("clear"));
                        sendMessage(chatId, "Коллекция очищена");
                        currentUserSpaceMarines.clear();
                        save(chatId);
                        break;
                    case "info":
                        sendMessage(chatId, cm.info());
                        break;
                    case "filter_starts_with_name":
                        String answer = cm.filterStartsWithName(args[0]);
                        if (answer.equals("[]")) {
                            answer = "Нет элементов, значение поля name которых начинается с этой подстроки";
                        }
                        sendMessage(chatId, answer);
                        break;
                    case "count_greater_than_heart_count":
                        try {
                            Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "Некорректное значение heartCount. Пожалуйста, введите целое число.");
                            break;
                        }
                        long count = cm.countGreaterThanHeartCount(args[0]);
                        sendMessage(chatId, String.format("В коллекции %d элементов, значение поля heartCount которых больше заданного", count));
                        break;
                    case "print_field_ascending_health":
                        String healthList = cm.printFieldAscendingHealth(cm.spaceMarines).toString();
                        if (healthList.isEmpty()) {
                            sendMessage(chatId, "Коллекция пуста");
                        } else {
                            sendMessage(chatId, healthList);
                        }
                        break;
                    case "show_all_space_marines":
                        StringBuilder builder = new StringBuilder();
                        for (TreeSet<SpaceMarine> spaceMarines : allSpaceMarines.values()){
                            for (SpaceMarine spaceMarine : spaceMarines){
                                builder.append(spaceMarine.toString()).append("\n");
                            }

                        }
                        sendMessage(chatId, builder.toString());
                        break;
                    default:
                        sendMessage(chatId, "Такой команды нет, повторите попытку");

                }
            }
            save(chatId);
        }



    }

    private void startCommandReceived(long chatId, String name) {
        sendMessage(chatId, "Здравствуйте, " + name + "!\nВведите команду (для получения списка всех команд введите \"help\"):");

    }

    public SpaceMarine addCommandReceived(long chatId, String[] args) {
        SpaceMarine spaceMarine = null;
        if (args.length!=9){
            sendMessage(chatId, "Введите данные для добавления нового космического корабля в формате: \n\nadd name x y health heartCount astartesCategory meleeWeapon chapterName marinesCount");
        }
        else {
            try {
                for(int i = 0; i < 8; i++) {
                    args[i] = args[i].trim();
                }
                String name = args[0];
                Coordinates coordinates = new Coordinates(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                double health = Double.parseDouble(args[3]);
                int heartCount = Integer.parseInt(args[4]);
                AstartesCategory category = AstartesCategory.valueOf(args[5]);
                MeleeWeapon meleeWeapon = MeleeWeapon.valueOf(args[6]);
                Chapter chapter = new Chapter(args[7], Integer.parseInt(args[8]));
                spaceMarine = new SpaceMarine(name, coordinates, health, heartCount, category, meleeWeapon, chapter);
            }
            catch (Exception e) {
                sendMessage(chatId, "Некорректные данные для добавления космического корабля. Проверьте введенные значения.\n\n" +
                        "long id; Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически\n" +
                        "String name; Поле не может быть null, Строка не может быть пустой\n" +
                        "Coordinates coordinates; Поле не может быть null\n" +
                        "java.time.LocalDateTime creationDate; Поле не может быть null, Значение этого поля должно генерироваться автоматически\n" +
                        "float health; Значение поля должно быть больше 0\n" +
                        "Long heartCount; Поле не может быть null, Значение поля должно быть больше 0, Максимальное значение поля: 3\n" +
                        "AstartesCategory category; Поле может быть null\n" +
                        "MeleeWeapon meleeWeapon; Поле не может быть null\n" +
                        "Chapter chapter; Поле может быть null");

            }

        }
        return spaceMarine;
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
        }
    }
    public synchronized void save(long chatId){
        allSpaceMarines.put(chatId, currentUserSpaceMarines);
        cm.save(allSpaceMarines);


    }

    private synchronized void startConsoleInputHandler() {
        new Thread(() -> {
            while (true) {
                allSpaceMarines = databaseManager.getSpaceMarines();
            }
        }).start();
    }
}
