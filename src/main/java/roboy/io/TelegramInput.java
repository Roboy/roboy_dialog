package roboy.io;

import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.ConversationManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Is a singleton because there is only one key. Handles telegram API and hands threads their respective messages.
 * Use getInstance() for access.
 */
public class TelegramInput implements InputDevice {
    private static TelegramInput telegraminputInstance = TelegramInput.getInstance();
    private static final Logger logger = LogManager.getLogger();

    private static final HashMap<Long, BlockingQueue<String>> newMessages = new HashMap<>(); //maps threadID to message queue since we can access threadID

    private TelegramInput(){//since this is half-static, the constructor is empty
    }

    /**
     * Gets called whenever a new telegram message arrives.
     * Places them in the appropriate thread's queue. Creates queue and thread before if necessary.
     * @param update contains a (sender uuid,message) string pair.
     */
    public static void onUpdate(Pair<String, String> update) {

        String uuid = "telegram-" + update.getKey();

        //get ThreadID
        Long cThreadID = ConversationManager.getConversationThreadID(uuid);

        if (cThreadID == null){//if Thread does not exist yet, create it and it's queue
            try {
                ConversationManager.spawnConversation(uuid);
            } catch (IOException e) {
                logger.error("Could not create conversation for telegram uuid '" + update.getKey() + "'!");
                return;
            }
            cThreadID = ConversationManager.getConversationThreadID(uuid);
            synchronized (newMessages) {
                newMessages.put(cThreadID, new LinkedBlockingQueue<>());
            }
        }
        //put message in correct queue
        newMessages.get(cThreadID).add(update.getValue());
    }

    /**
     * Provides the TelegramInput singleton instance.
     * @return telegram instance
     */
    public static TelegramInput getInstance(){
        if(telegraminputInstance == null) {
            //now: Thread safety
            synchronized (TelegramInput.class) {
                if(telegraminputInstance == null) telegraminputInstance = new TelegramInput();
            }
        }
        return telegraminputInstance;
    }

    /**
     * Thread waits in listen() until a new input is provided, then returns with said input.
     */
    @Override
    public Input listen() throws InterruptedException {
        BlockingQueue<String> queue = newMessages.get(Thread.currentThread().getId());
        return new Input(queue.take());
    }
}