package roboy.io;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.ConversationManager;
import roboy.util.Pair;

import java.io.IOException;
import java.util.HashMap;

/**
 * Handles telegram API and hands threads their respective messages.
 *
 * TelegramInput is design to be allocated for each conversation.
 * Each new user that is communicating with roboy via Telegram will make a new TelegramInput in the system.
 * On the other hand, the TelegramInputs that is already existing in the system will just be informed about the messages.
 */
public class TelegramInput implements InputDevice, CleanUp {
    //static part since there is only one API handle
    private static final Logger logger = LogManager.getLogger();
    private static final HashMap<String, TelegramInput> inputDevices = new HashMap<>(); //maps uuid to InputDevices so we can sort messages into them

    private volatile String message; //one input message per thread; is volatile since the TelegramAPI thread calls onUpdate and writes into it while the Conversation thread reads it in listen()


    /**
     * Creates a Telegraminput device that sorts incoming messages from the telegram handle to the individual conversations
     * @param uuid The uuid of the interlocutor must be formed like this: "telegram-[uuid from service]"
     */
    public TelegramInput(String uuid){//since this is half-static, the constructor is empty
        this.message = "";
        synchronized (inputDevices){//place this in hashmap so we can find it when we want to deliver a new message
            inputDevices.put(uuid, this);
        }
    }

    /**
     * Gets called by the TelegramAPI Thread whenever a new telegram message arrives.
     * Places them in the appropriate thread's message string. Creates thread beforehand, if necessary.
     * @param update contains a (sender uuid,message) string pair.
     */
    public static void onUpdate(Pair<String, String> update, String name) {

        String chatId = update.getKey();
        String uuid = "telegram-" + chatId;

        TelegramInput input = inputDevices.get(uuid);

        if (input == null){//if Thread does not exist yet, create it and place the new message as it's input
            try {
                ConversationManager.spawnConversation(uuid, name);
            } catch (IOException e) {
                logger.error("Could not create conversation for telegram uuid '" + chatId + "'!");
                return;
            }
            input = inputDevices.get(uuid);
        }
        //add message to the corresponding conversations input
        synchronized (input) {
            input.message += " " + update.getValue();
            //make thread do work!
            input.notify();
        }
    }

    /**
     * Thread waits in listen() until a new input is provided and the thread is interrupted, then returns with said input.
     * If the thread is interrupted without Input waiting to be consumed, listen() throws an IOException
     * @throws InterruptedException: InterruptedException thrown by the thread when interrupted while wait()ing
     */
    @Override
    public Input listen() throws InterruptedException {
        Input newInput;
        synchronized (this) {
            while(message.equals("")){//while no new messages for this thread exist: wait
                try {
                    this.wait();
                }catch (InterruptedException e) {//Thread woke up! Process new information!
                    if(message == null || message.equals("")){//if this interrupt was not triggered because new messages arrived, throw exception to be handled
                        throw e;
                    }
                }
            }
            newInput = new Input(message);
            message = ""; //consume message
        }
        return newInput;
    }

    /**
     * Deregisters the instance from the static ledger. Must be called when it should be destroyed or it will stay in memory until the end of operation.
     */
    @Override
    public void cleanup() {
        inputDevices.values().remove(this);
    }
}