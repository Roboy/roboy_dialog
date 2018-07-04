package roboy.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.action.Action;
import roboy.dialog.action.EmotionAction;
import roboy.dialog.action.SpeechAction;
import roboy.util.TelegramCommunicationHandler;

import java.util.List;

public class TelegramOutput implements OutputDevice {

    private TelegramCommunicationHandler communicationHandler = TelegramCommunicationHandler.getInstance();
    private final static Logger logger = LogManager.getLogger();
    private String uuid;

    /**
     * Handles sending messages to the TelegramAPI from the DialogSystem
     * @param uuid The uuid of the interlocutor must be formed like this: "telegram-[uuid from service]"
     */
    public TelegramOutput(String uuid) {
        this.uuid = uuid.substring(uuid.indexOf('-')+1);
        logger.info("Creating TelegramOutput for " + uuid + "! Setting this.uuid to " + this.uuid + "...");
        logger.error("output initialized with: "+uuid);
    }

    /**
     * Carries out actions in the telegram way:
     * Speechactions are sent as text messages via telegram,
     * EmotionActions are sent as stickers via telegram
     * @param actions Actions to be carried out on the telegram service
     */
    @Override
    public void act(List<Action> actions) {
        for(Action a : actions) {
            if (a instanceof SpeechAction) {
                String message = ((SpeechAction) a).getText();
                communicationHandler.sendMessage(message, this.uuid);
            }else if (a instanceof EmotionAction) {
                String stickerID = null;
                switch(((EmotionAction) a).getState()){
                    case "shy": stickerID = "CAADAgADSwAD5dCAEBGmde8-twTLAg"; break;
                    case "smileblink": stickerID = "CAADAgADSgAD5dCAEMQakIa3aHHSAg"; break;
                    case "kiss": stickerID = "CAADAgADOQAD5dCAEOtbfZz0NKh2Ag"; break;
                    case "lookleft": //same as lookright
                    case "lookright": stickerID = "CAADAgADFQAD5dCAEKM0TS8sjXiAAg"; break;
                }
                if(stickerID != null) communicationHandler.sendSticker(this.uuid, stickerID);


            }
        }
    }
}
