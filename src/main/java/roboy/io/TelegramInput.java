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

    private static final HashMap<Long, String> newMessages = new HashMap<>(); //maps threadID to message queue since we can access threadID

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

        if (cThreadID == null){//if Thread does not exist yet, create it and place the new message as it's input
            try {
                ConversationManager.spawnConversation(uuid);
            } catch (IOException e) {
                logger.error("Could not create conversation for telegram uuid '" + update.getKey() + "'!");
                return;
            }
            cThreadID = ConversationManager.getConversationThreadID(uuid);
            synchronized (newMessages) {
                newMessages.put(cThreadID, update.getValue());
            }
        }
        else {
            //add message to the corresponding conversations input
            synchronized (newMessages) {
                newMessages.replace(cThreadID, newMessages.get(cThreadID) + " " + update.getValue());
            }
        }
        //make thread do work!
        ConversationManager.interruptConversation(uuid);
    }

    /**
     * Provides the TelegramInput singleton instance.
     * @return telegram instance
     */
    public static TelegramInput getInstance(){
        if(telegraminputInstance == null) {//for speed
            //now: Thread safety
            synchronized (TelegramInput.class) {
                //to prevent magic edgecases from ruining our day
                if(telegraminputInstance == null) telegraminputInstance = new TelegramInput();
            }
        }
        return telegraminputInstance;
    }

    /**
     * Thread waits in listen() until a new input is provided and the thread is interrupted, then returns with said input.
     * If the thread is interrupted without Input waiting to be consumed, listen() throws an IOException
     * @throws InterruptedException: InterruptedException thrown by the thread when interrupted while wait()ing
     */
    @Override
    public Input listen() throws InterruptedException {
        String message;
        synchronized (newMessages) {
            message = newMessages.get(Thread.currentThread().getId());//try to read new messages
            while(message.equals("")){//while no new messages for this thread exist: wait
                try {
                    newMessages.wait();
                }
                catch (InterruptedException e) {//Thread woke up! Process new information!
                    message = newMessages.get(Thread.currentThread().getId());
                    if(message == null || message.equals("")){//if this interrupt was not triggered because new messages arrived, throw exception to be handled
                        throw e;
                    }
                }
            }
            newMessages.replace(Thread.currentThread().getId(), ""); //consume message
        }
        return new Input(message);
    }
}