package roboy.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.util.TelegramBotBoyPolling;

public class BotBoyInput implements InputDevice  {
    private final static Logger logger = LogManager.getLogger();
    private TelegramBotBoyPolling polling = TelegramBotBoyPolling.getInstance();

    @Override
    public Input listen() throws InterruptedException, IOException {
        Pair<String, String> pairFromPolling;
        pairFromPolling = polling.getInput();

        String chatID = pairFromPolling.getKey();
        String message = pairFromPolling.getValue();

        // HASH MAPs are outdated
        logger.error("hi from input: "+message);
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("chat-id", chatID);

        Input result = new Input(message, attributes);

        return result;
    }

}
