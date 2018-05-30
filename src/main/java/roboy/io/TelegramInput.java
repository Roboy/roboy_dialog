package roboy.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

/**
 * Is a singleton because there is only one key. Handles telegram API and hands threads their respective messages.
 */
public class TelegramInput implements InputDevice {
    private static TelegramInput telegraminputInstance = TelegramInput.getInstance();

    private static final HashMap<Long, BlockingQueue<String>> newMessages = new HashMap<>(); //maps threadID to message queue since we can access threadID

    private TelegramInput(){
    }

    public static void onUpdate(){
        // on new interlocutor: newMessages.put(ConversationManager.getConversationThreadID(chatID), new List<String>);

        //newMessages.get(ConversationManager.getConversationThreaDID(chatID)).add(message);




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
        BlockingQueue<String> queue = newMessages.get(Thread.currentThread().getId());
        return new Input(queue.take());
    }
}
