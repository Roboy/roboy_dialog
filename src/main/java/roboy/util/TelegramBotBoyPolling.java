package roboy.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.*;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import roboy.io.InputDevice;
import roboy.io.OutputDevice;

/** Singleton Class For Telegram Bot */
public class TelegramBotBoyPolling extends TelegramLongPollingBot {
    private final static Logger logger = LogManager.getLogger();

    //TODO: separate file for the sake of privacy
    public static final String TOKEN= "574580969:AAHt-O5BZu1kM864mrNRoPmHhsjW0Y_tTiQ";
    public static final String BOT_USERNAME = "RoboyTestBot";

    private InputDevice inputDeviceListener;
    private OutputDevice outputDeviceListener;
    private volatile String chatID;
    private volatile String message = "";
    private volatile boolean messageSet = false;

    // Instance for singleton
    private static TelegramBotBoyPolling instance;

    public static TelegramBotBoyPolling getInstance(){
        if(instance == null){
            instance = new TelegramBotBoyPolling();
        }
        return instance;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()){
            Message message = update.getMessage();
            if(message.hasText()){
                this.chatID = message.getChatId().toString();
                String text = message.getText();
                this.message = text;
                logger.error("Message set is changed: "+ this.messageSet);
                messageSet = true;
                logger.error("Message set is changed: "+ this.messageSet);
                logger.info("text: "+text);
                //TODO: trigger the listen function in "telegrambotboy"
            }
        }
    }

    public void sendMessage(String message){
        SendMessage sendMessageRequest = new SendMessage();
        if(chatID == null){
            logger.error("Unexpected null chat ID");
            return;
        }
        sendMessageRequest.setChatId(chatID.toString()); //who should get the message? the sender from which we got the message...
        sendMessageRequest.setText(message);
        try {
            sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
        } catch (TelegramApiException e) {
            //do some error handling
        }//end catch()
    }

    public String getInput(){
        logger.error("Loop is starting");
        //TODO:
        while(!this.messageSet){
            //wait
        }
        logger.error("exit the loop");
        messageSet = false;
        return this.message;
    }


    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

}
