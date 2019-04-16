package com.telegram.bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

/**
 * @author Egor
 * This class is Telegram bot for getting weather info from Telegram api
 */
public class WeatherBot extends TelegramLongPollingBot {
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
        Message message = update.getMessage();
        if(message != null && message.hasText()){
            String command = message.getText();
            switch (command){
                case "/help": answer(message, "Чем я могу помочь?");
                    break;
                case "/settings": answer(message, "Выберите настройку");
                    break;
                default : answer (message, "Команда неизвестна");
                    break;
            }
        }

    }

    private void answer(Message message, String answer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(answer);
        sendAnswer(message, sendMessage);
    }

    /**
     * Возвращает имя бота, указанного при регистрации
     */
    public String getBotUsername() {
        return "WeatherBuddyBot";
    }
    /**
     * @author Egor
     * Возвращает токен
     */
    public String getBotToken() {
        return "841317489:AAHl_I3RhOZ_EWF-r5dmb-L_qrunA1rkugA";
    }

    private void sendAnswer(Message message, SendMessage sendMessage) {
        try {
            execute(sendMessage.setText(
                    message.getText()
                    +" -> "
                    +sendMessage.getText()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
