package roboy.io;

import javafx.util.Pair;
import org.apache.jena.atlas.logging.Log;
import roboy.dialog.ConversationManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Is a singleton because there is only one key. Handles telegram API and hands threads their respective messages.
 */
public class TelegramInput implements InputDevice {
    private static TelegramInput telegraminputInstance = TelegramInput.getInstance();

    private static final HashMap<Long, BlockingQueue<String>> newMessages = new HashMap<>(); //maps threadID to message queue since we can access threadID
    private static List<String> chatIdList = new ArrayList<>();

    private TelegramInput(){

    }

    public static void onUpdate(Pair<String, String> pair){
        String chatID = pair.getKey();
        String message = pair.getValue();

        if(!chatIdList.contains(chatID)){
            // chat id hasn't encountered! add it to list and spawn the conversation
            chatIdList.add(chatID);

            try {
                ConversationManager.spawnConversation(chatID);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Long conv = ConversationManager.getConversationThreadID(chatID);
        if(!newMessages.containsKey(conv)){
            BlockingQueue<String> queue = new LinkedBlockingQueue<>();
            newMessages.put(conv, queue);
        }
        newMessages.get(conv).add(message);

//        Log.error(TelegramInput.class, "chatID: " + chatID);
//        Log.error(TelegramInput.class, "message: " + message);
//        Log.error(TelegramInput.class, "conv: " + conv.toString());

    }

    public static TelegramInput getInstance(){
        if(telegraminputInstance == null) {
            //now: Thread safety
            synchronized (TelegramInput.class) {
                if(telegraminputInstance == null) telegraminputInstance = new TelegramInput();
            }
        }
        return telegraminputInstance;
    }

    @Override
    public Input listen() throws InterruptedException {//TODO: uses ConversationManager to sort input into threads; make threadsafe
        Log.error(this,"hello");
        BlockingQueue<String> queue = newMessages.get(Thread.currentThread().getId());
        return new Input(queue.take());
    }
}
