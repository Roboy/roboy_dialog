package roboy.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;
import roboy.util.TelegramPolling;

import java.util.List;

public class TelegramOutput implements OutputDevice {

    private TelegramPolling polling = TelegramPolling.getInstance();
    private final static Logger logger = LogManager.getLogger();
    private String uuid;

    public TelegramOutput(String uuid) {
        this.uuid = uuid.substring(uuid.indexOf('-')+1);
        logger.info("Creating TelegramOutput for " + uuid + "! Setting this.uuid to " + this.uuid + "...");
        logger.error("output initialized with: "+uuid);
    }

    @Override
    public void act(List<Action> actions) {
        for(Action a : actions) {
            if (a instanceof SpeechAction) {
                String message = ((SpeechAction) a).getText();
                logger.error("sending this chatID: "+this.uuid);
                polling.sendMessage(message, this.uuid);
            }
        }
    }
}
