package com.telegram.bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Egor
 * Класс бота для получения данных о погоде
 */
public class WeatherBot extends TelegramLongPollingBot {
    private Map<String, String> existingRequests = new HashMap<>();

    public static void main(String[] args) {
        System.getProperties().put("proxySet", "true");
        System.getProperties().put("socksProxyHost", "127.0.0.1");
        System.getProperties().put("socksProxyPort", "9150");

        ApiContextInitializer.init();
        TelegramBotsApi botApi = new TelegramBotsApi();

        try{
            botApi.registerBot(new WeatherBot());

        } catch (TelegramApiRequestException e){
            e.printStackTrace();
        }
    }
    /**
     * Метод для приёма обновлений через LongPull
     * @see org.telegram.telegrambots.meta.generics.LongPollingBot
     */
    public void onUpdateReceived(Update update) {
        SendMessage sendMessageService = new SendMessage();
        Message message = update.getMessage();
        createButtons(sendMessageService);

        existingRequests.put("/help", "Чем я могу помочь?");
        existingRequests.put("/settings", "Выберите настройку");

        if(message != null && message.hasText()){
            processTextCommand(message);
        }
        }
    /**
     * Обрабатывает команды, содержащие текст
     */
    private void processTextCommand(Message message) {
        String command = message.getText();
        answer(message, existingRequests.get(command));
    }

    /**
     * Создаёт кнопки чата на основе текста опознаваемых реквестов
     * @param sendMessageService
     */
    private void createButtons(SendMessage sendMessageService){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();

        firstRow.forEach(row->firstRow.add(
                new KeyboardButton(existingRequests.keySet().iterator().next())));
        keyboard.add(firstRow);

        keyboardMarkup.setSelective(true)
                 .setResizeKeyboard(true)
                 .setOneTimeKeyboard(false)
                 .setKeyboard(keyboard);
        sendMessageService.setReplyMarkup(keyboardMarkup);
    }

    /**
     * Посылает в чат ответ на принятый от пользователя запрос запрос
     * @param message принятый объект сообщения запроса
     * @param answer строка, посылаемая в ответ на запрос
     */
    private void answer(Message message, String answer) {
        if(answer == null) {
            answer = "Неизвестная команда";
        }
        SendMessage answerMessage = createAnswer(message, answer);
        sendAnswer(answerMessage);
    }

    /**
     * Возвращает имя бота, указанного при регистрации
     */
    public String getBotUsername() {
        return "WeatherBuddyBot";
    }
    /**
     * Возвращает токен
     */
    public String getBotToken() {
        return "841317489:AAHl_I3RhOZ_EWF-r5dmb-L_qrunA1rkugA";
    }

    /**
     * Посылает ответ в чат
     * @param sendMessageService
     */
    private void sendAnswer(SendMessage sendMessageService) {
        try {
            execute(sendMessageService);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param message
     * @param answerText
     * @return
     */
    private SendMessage createAnswer (Message message, String answerText) {
        SendMessage sendMessage = new SendMessage();
        createButtons(sendMessage);
        sendMessage.enableMarkdown (true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(answerText);
        return sendMessage;
    }
}
