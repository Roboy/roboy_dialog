package roboy.util;

import javafx.util.Pair;
import org.apache.commons.io.IOUtils;
import org.apache.jena.atlas.logging.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.ActionType;
import org.telegram.telegrambots.api.methods.send.SendChatAction;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.send.SendSticker;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.stickers.Sticker;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import roboy.io.InputDevice;
import roboy.io.OutputDevice;
import roboy.io.TelegramInput;

import java.io.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/** Singleton Class For Telegram Bot */
public class TelegramPolling extends TelegramLongPollingBot implements Timeout.TimeoutObserver{
    private final static Logger logger = LogManager.getLogger();

//    public static final String TOKEN;
//    public static final String BOT_USERNAME;

    private static final String tokensPath = "/Users/Apple/botboy/tokens.json";
    private static final int TYPING_TIME_LIMIT = 3; //SECONDS
    private static final int INPUT_TIME_LIMIT = 5;

    private InputDevice inputDeviceListener;
    private OutputDevice outputDeviceListener;

    private volatile String chatID;

    private final Object syncObject = new Object();

    // collection of all messages.
    private List<Message> messages  = new ArrayList<Message>();

    // CHAT ID ----- ITS MESSAGE
    private List<Pair<String, String>> pairs = new ArrayList<Pair<String, String>>();

    //Timeout
    private List<Timeout> telegramTimeouts;

    public TelegramPolling(){
        super();
        telegramTimeouts = new ArrayList<>();

    }

    // Instance for singleton
    private static TelegramPolling instance;

    public static TelegramPolling getInstance(){
        if(instance == null){
            instance = new TelegramPolling();
        }

        return instance;
    }

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


    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()){
            Message message = update.getMessage();
            User user = message.getFrom();

            if(message.hasText()){
                String chatID = message.getChatId().toString();
                String text = message.getText();
                if(text.startsWith("/")){
                    //inline command
                }else{
                    try {
                        //get message, add it to containers

                        pairs.add(new Pair<String, String>(chatID, text));
                        messages.add(message);

                        //wait for certain seconds, start the timer
                        //telegramTimeout.start(this);
                        handleTimeout(chatID);

                        //notifyInputDevice();
                    } catch (Exception e) {
                        Log.error(this, "Message receiving has been interrupted.");
                        e.printStackTrace();
                    }
                }
            }
        }else{
            //The update does not have messages
            Log.error(this,"update: "+update.toString());
        }
    }

    @Override
    public void onTimeout(String chatID) {

        // timeout method for getting all the messages
        //telegramTimeout.stop();
        for(Timeout t: telegramTimeouts){
            if(t.getUnique().equals(chatID)){
                t.stop();
            }
        }

        // get the all messages
        Pair<String, String> result = null;
        for(Pair p: pairs){
            if(!chatID.equals(p.getKey())){
                continue;
            }

            String message = p.getValue().toString();

            // check if the result initialized
            if(result == null) {
                result = new Pair<String, String>(chatID, message);
            }
            else {
                // sum all of the messages
                String newMessage = result.getValue().toString()+ " " + message;

                //equal chat id
                result = new Pair<String, String>(chatID, newMessage);
            }
        }

        TelegramInput.onUpdate(result);

        // notify the input devic
    }

    // FIXME: DO NOT DELETE THE CODE BLOCKS BELOW
//    public void notifyInputDevice(){
//        synchronized(syncObject) {
//            syncObject.notify();
//        }
//    }
//
//    // tries to wait untill a message received
//    public Pair<String, String> getInput(){
//
//        synchronized(syncObject) {
//            try {
//                while(pairs.isEmpty()) {
//                    syncObject.wait();
//                }
//
////                NOW: gets all of the messages
//                Pair<String, String> result = null;
//                String allMessages = "";
//                for(Pair p: pairs){
//                    String chatID = p.getKey().toString();
//                    String message = p.getValue().toString();
//
//                    // check if the result initialized
//                    if(result == null){
//                        result = new Pair<String, String>(chatID, message);
//                    }else{
//                        // sum all of the messages
//                        String newMessage = result.getValue().toString()+message;
//                        String newChatID = result.getKey();
//                        if(newChatID.equals(chatID)){
//                            //equal chat id
//                            result = new Pair<String, String>(newChatID, newMessage);
//                        }
//                    }
//                }
//
//                //remove all of the pairs
//                pairs.clear();
//                messages.clear();
//                if(pairs.isEmpty()){
//                    Log.error(this, "the list is empty, this is working");
//                    Log.error(this, "result message: "+result.getValue().toString());
//                }
//
//                if(result != null){
//                    this.chatID = result.getKey();
//                    return result;
//                }
//                else
//                    return new Pair<>("","");
//            } catch (InterruptedException e) {
//                // Happens if someone interrupts your thread.
//                return new Pair<String, String>("","");
//            }
//        }
//    }

    // ---- Collection handler METHODS ----

    //get all messages from user ID
    public List<String> getMessagesFromUserID(int userID){
        List<String> result = new ArrayList<String>();

        for(Message m: messages){
            int id = m.getFrom().getId();
            if(id == userID){
                if(m.hasText()){
                    result.add(m.getText());
                }
            }
        }
        return result;
    }


    // ---- SEND METHODS ----

    //Tries to send message with a string and chatID
    public void sendMessage(String message, String chatID){
        try {
            sendTypingFromChatID(this.chatID);

            //FIXME: this cause some bug, fix it.
            TimeUnit.SECONDS.sleep(TYPING_TIME_LIMIT);

            SendMessage sendMessageRequest = new SendMessage();
//            sendMessageRequest.setChatId(chatID); //who should get the message? the sender from which we got the message...
            sendMessageRequest.setChatId(this.chatID);
            sendMessageRequest.setText(message);
            try {
                execute(sendMessageRequest);
            } catch (TelegramApiException e) {
                //do some error handling
            }//end catch()

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Tries to send the given sticker to given chatID
    public void sendSticker(String chatID, String stickerId){
        SendSticker sendStickerRequest = new SendSticker();
        try {
            sendStickerRequest.setChatId(this.chatID);
            sendStickerRequest.setSticker(stickerId);

            sendSticker(sendStickerRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //tries to send typing action to given message
    public void sendTypingFromMessageObject(Message message){
        try {
            SendChatAction action = new SendChatAction();
            action.setChatId(message.getChatId());
            action.setAction(ActionType.TYPING);
            execute(action);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //tries to send typing action with chatID
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