package roboy.util;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.*;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import roboy.io.InputDevice;
import roboy.io.OutputDevice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/** Singleton Class For Telegram Bot */
public class TelegramBotBoyPolling extends TelegramLongPollingBot {
    private final static Logger logger = LogManager.getLogger();

    //TODO: separate file for the sake of privacy
//    public static final String TOKEN;
//    public static final String BOT_USERNAME;

    public static final String tokensPath = "/Users/Apple/botboy/tokens.json";

    private InputDevice inputDeviceListener;
    private OutputDevice outputDeviceListener;
    private volatile String chatID;
    private volatile String message = "";
    private volatile boolean messageSet = false;

    public TelegramBotBoyPolling(){
        super();
    }

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
                //FIXME: chatID and text shouldn't be global variables
                String text = message.getText();
                if(text.startsWith("/")){
                    //inline command


                }else{

                    this.message = text;
                    messageSet = true;
                    //TODO: trigger the listen function in "telegrambotboy"
                }
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
        //TODO:
        while(!this.messageSet){
            //wait
        }

        messageSet = false;
        return this.message;
    }

    @Override
    public String getBotUsername(){
        return getJsonString("BOT_USERNAME");
    }

    @Override
    public String getBotToken() {
        return getJsonString("TOKEN");
    }

    private String getJsonString(String key){
        String result = "";
        File f = new File(tokensPath);
        if (f.exists()){
            try{
                InputStream is = new FileInputStream(tokensPath);
                String jsonTxt = IOUtils.toString(is,"UTF-8");
                JSONObject obj = new JSONObject(jsonTxt);
                result = obj.getString(key);
                return result;
            }catch(IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

}
