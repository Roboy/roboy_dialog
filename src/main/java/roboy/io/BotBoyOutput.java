package roboy.io;

import org.apache.jena.atlas.logging.Log;
import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;
import roboy.dialog.action.TelegramAction;
import roboy.util.TelegramBotBoyPolling;

import java.util.List;

public class BotBoyOutput implements OutputDevice {
    private TelegramBotBoyPolling polling = TelegramBotBoyPolling.getInstance();
    private String lastChatID;
    @Override
    public void act(List<Action> actions) {
        for(Action a : actions){
            if(a instanceof TelegramAction){

                String message = ((TelegramAction) a).getText();
                String chatID = ((TelegramAction) a).getChatID();
                lastChatID = chatID;

                polling.sendMessage(message, chatID);
            }
            if(a instanceof SpeechAction){
                String message = ((SpeechAction) a).getText();
                polling.sendMessage(message, lastChatID);
            }
        }


    }
}
