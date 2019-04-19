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

import java.util.*;

/**
 * @author Egor
 * Класс бота для получения погодных данных
 */
public class WeatherBot extends TelegramLongPollingBot {
    private Properties knownRequests =
            new PropertiesService("known.requests.properties")
                   .getProperties();
    private Properties tokens =
            new PropertiesService("tokens.properties")
                   .getProperties();

    public static void main(String[] args) {
        SetSystemSettings();
        ApiContextInitializer.init();
        TelegramBotsApi botApi = new TelegramBotsApi();

        try{
            botApi.registerBot(new WeatherBot());

        } catch (TelegramApiRequestException e){
            e.printStackTrace();
        }
    }

    /**
     * Устанавливает прокси для соединения с Telegram Api.
     * Работает только если включен Tor Browser
     */
    private static void SetSystemSettings() {
        System.getProperties().put("proxySet", "true");
        System.getProperties().put("socksProxyHost", "127.0.0.1");
        System.getProperties().put("socksProxyPort", "9150");
    }

    /**
     * Метод для приёма обновлений через LongPull
     * @see org.telegram.telegrambots.meta.generics.LongPollingBot
     */
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if(message != null && message.hasText()){
            processTextCommand(message);
        }
    }
    /**
     * Обрабатывает команды, содержащие текст
     */
    private void processTextCommand(Message message) {
        String command = message.getText();
        answer(message, knownRequests.getProperty(command));
    }

    /**
     * Создаёт кнопки чата на основе текста опознаваемых реквестов
     * @param sendMessageService сервис оптправки ответов в чат
     */
    private void createButtons(SendMessage sendMessageService){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();

        knownRequests.stringPropertyNames().forEach((request)->
                firstRow.add(new KeyboardButton(request)));
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
     * Получает имя бота
     * @return имя зарегистрированного бота
     */
    public String getBotUsername() {
        return tokens.getProperty("telegram.bot.name");
    }

    /**
     * Получает токен
     * @return токен от Telegram Api
     */
    public String getBotToken() {
        return tokens.getProperty("telegram.token");
    }

    /**
     * Посылает ответ в чат
     * @param sendMessageService сервис отправки ответов
     */
    private void sendAnswer(SendMessage sendMessageService) {
        try {
            execute(sendMessageService);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создаёт ответ в чат на основе параметров запроса
     * @param message объект принимаемого запроса
     * @param answerText текст получаемого пользователем ответа
     * @return модифицированный объект сервиса отправки ответа
     */
    private SendMessage createAnswer (Message message, String answerText) {
        SendMessage sendMessageService = new SendMessage();
        sendMessageService.enableMarkdown(true)
                .setChatId(message.getChatId().toString())
                .setReplyToMessageId(message.getMessageId())
                .setText(answerText);
        createButtons(sendMessageService);
        return sendMessageService;
    }
}
