package roboy.util;

import org.telegram.telegrambots.TelegramBotsApi;
import roboy.util.Pair;
import org.apache.commons.io.IOUtils;
import org.apache.jena.atlas.logging.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.ActionType;
import org.telegram.telegrambots.api.methods.send.SendChatAction;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.send.SendSticker;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import roboy.io.TelegramInput;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/** Singleton Class For Telegram Bot */
public class TelegramCommunicationHandler extends TelegramLongPollingBot implements Timeout.TimeoutObserver{
    private final static Logger logger = LogManager.getLogger();

    private static final String tokensPath = ConfigManager.TELEGRAM_API_TOKENS_FILE;//place path to your token file here
    private static final int TYPING_TIME_LIMIT = 3; //SECONDS
    private static final int INPUT_TIME_LIMIT = 5; //SECONDS

    // CHAT ID ----- ITS MESSAGE
    private volatile List<Pair<String,Pair<String, String>>> pairs = new ArrayList<>();
    private List<Timeout> telegramTimeouts; //Timeouts
    private final static int initTime = (int) (System.currentTimeMillis() / 1000L); //in order to discard messages older than launch

    private TelegramCommunicationHandler(){
        super();
        telegramTimeouts = new ArrayList<>();
        if(tokensPath.equals("")) throw new InvalidParameterException("Telegram tokens are not provided. Please provide them via config.properties!");
    }

    // Instance for singleton
    private static TelegramCommunicationHandler instance;

    public static TelegramCommunicationHandler getInstance(){
        if(instance == null){//for speed purposes
            synchronized (TelegramCommunicationHandler.class){//in here: for threadsafety
                //to prevent magic edgecases ruining our day
                if(instance == null) instance = new TelegramCommunicationHandler();
            }
        }
        return instance;
    }

    /**
     * Waits until specified time passed after the last message w.r.t. given chatId.
     * @param chatID unique identifier for a chat.
     */
    private void handleTimeout(String chatID){
        int millisecond = 1000;

        // if the list is empty create the first timeout and set it
        if(telegramTimeouts.isEmpty()){
            Timeout t  = new Timeout(millisecond * INPUT_TIME_LIMIT);
            t.setUnique(chatID);

            telegramTimeouts.add(t);
        }

        boolean hasStarted = false;
        for(Timeout t: telegramTimeouts){
            if(t.getUnique().equals(chatID)){
                hasStarted = true;
                t.start(this);
            }
        }

        if(!hasStarted){
            // there is no timeout for given chatID so start one
            Timeout t  = new Timeout(millisecond * INPUT_TIME_LIMIT);
            t.setUnique(chatID);
            t.start(this);

            telegramTimeouts.add(t);
        }
    }

    /**
     * Receives the updates from telegram's api and called by it.
     * @param update consist of an update of a chat.
     */
    @Override
    public void onUpdateReceived(Update update) {
        String name = null;
        if(!update.hasMessage()){
            return;
        }

        Message message = update.getMessage();
        // the message could be a sticker, a photo or a file but does not have any text to interpret
        if(!message.hasText()) return;
        if(message.getDate() < initTime) return;//Discard messages from before launch

        String chatID = message.getChatId().toString();
        String text = message.getText();
        if(text.startsWith("/")){
            //inline command
        }else{
            if(message.getFrom().getUserName() != null){
                name = message.getFrom().getUserName();
            }
            else if(message.getFrom().getFirstName() != null) {
                name = message.getFrom().getFirstName();
            }
            else{
                name = "Telegram user " + message.getFrom().getId().toString();
            }
            try {
                //get message, add it to containers
                pairs.add(new Pair<>(name, new Pair<>(chatID, text)));

                //wait for certain seconds, start the timer
                handleTimeout(chatID);
            } catch (Exception e) {
                Log.error(this, "Message receiving has been interrupted.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Called when specified time passed w.r.t. unique chatID.
     * Concatenate all the messages that is not processed.
     * Calls the InputDevice for telegram
     * @param chatID unique identifier for a chat.
     */
    @Override
    public void onTimeout(String chatID) {

        // timeout method for getting all the messages
        //telegramTimeout.stop();
        for(Timeout t: telegramTimeouts){
            if(t.getUnique().equals(chatID)){
                t.stop();
            }
        }

        List<Pair<String,Pair<String, String>>> removedObjects = new ArrayList<>();

        // get the all messages
        Pair<String,Pair<String, String>> result = null;
        for(Pair<String,Pair<String,String>> pa : pairs){
            Pair<String,String> p = pa.getValue();
            String name = pa.getKey();
            //Map a = new HashMap<String, String>();
            if(!chatID.equals(p.getKey())){
                continue;
            }

            removedObjects.add(pa);
            String message = p.getValue();

            // check if the result initialized
            if(result == null) {
                result = new Pair<>(name,new Pair<>(chatID, message));
            } else {
                // sum all of the messages
                String newMessage = result.getValue()+ " " + message;

                //equal chat id
                result = new Pair<>(name,new Pair<>(chatID, newMessage));
            }
        }


        if(result != null) {
            // notify the input device
            pairs.removeAll(removedObjects);
            TelegramInput.onUpdate(result.getValue(), result.getKey());
        }
    }

    // ---- SEND METHODS ----

    /**
     * Called from the OutputDevice when a message desired to send
     * Initiates the "typing status" and waits for a specified time
     * Sends the message afterwards
     * @param chatID unique identifier for a chat.
     */
    public void sendMessage(String message, String chatID){
        try {
            sendTypingFromChatID(chatID);

            TimeUnit.SECONDS.sleep(TYPING_TIME_LIMIT);

            SendMessage sendMessageRequest = new SendMessage();
            sendMessageRequest.setChatId(chatID);//who should get the message? the sender from which we got the message...
            sendMessageRequest.setText(message);
            try {
                execute(sendMessageRequest);
            } catch (TelegramApiException e) {
                Log.error(this, "Unable to send a message to telegram: "+e.getMessage());
                e.printStackTrace();
            }

        } catch (InterruptedException e) {
            Log.error(this, "Unable to send a message to telegram: "+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called from the OutputDevice when a message desired to send with stickers
     * Directly sends the sticker, without waiting for a specified time!
     * @param chatID unique identifier for a chat.
     * @param stickerId unique identifier for a sticker.
     */
    public void sendSticker(String chatID, String stickerId){
        SendSticker sendStickerRequest = new SendSticker();
        try {
            sendStickerRequest.setChatId(chatID);
            sendStickerRequest.setSticker(stickerId);

            sendSticker(sendStickerRequest);
        } catch (Exception e){
            Log.error(this, "Unable to send sticker: "+e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Sends the "typing" status to the telegram chat
     * @param chatID unique identifier for a chat.
     */
    public void sendTypingFromChatID(String chatID){
        try {
            SendChatAction action = new SendChatAction();
            action.setChatId(chatID);
            action.setAction(ActionType.TYPING);
            execute(action);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a image from url to the desired chat
     * @param url image's url
     * @param chatId unique identifier for a chat
     */
    public void sendImageFromUrl(String url, String chatId) {
        // Create send method
        SendPhoto sendPhotoRequest = new SendPhoto();
        // Set destination chat id
        sendPhotoRequest.setChatId(chatId);
        // Set the photo url as a simple photo
        sendPhotoRequest.setPhoto(url);
        try {
            // Execute the method
            sendPhoto(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a image from fileId to the desired chat
     * @param fileId a file id that is produced by telegram and using by it
     * @param chatId unique identifier for a chat
     */
    public void sendImageFromFileId(String fileId, String chatId) {
        // Create send method
        SendPhoto sendPhotoRequest = new SendPhoto();
        // Set destination chat id
        sendPhotoRequest.setChatId(chatId);
        // Set the photo url as a simple photo
        sendPhotoRequest.setPhoto(fileId);
        try {
            // Execute the method
            sendPhoto(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a file to the desired chat
     * @param filePath path of a file
     * @param chatId unique identifier for a chat
     */
    public void sendImageUploadingAFile(String filePath, String chatId) {
        // Create send method
        SendPhoto sendPhotoRequest = new SendPhoto();
        // Set destination chat id
        sendPhotoRequest.setChatId(chatId);
        // Set the photo file as a new photo (You can also use InputStream with a method overload)
        sendPhotoRequest.setNewPhoto(new File(filePath));
        try {
            // Execute the method
            sendPhoto(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Essential methods for the bot

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
        }else{
            Log.error(this, "Token file doesn't exist");
        }
        return result;
    }


}