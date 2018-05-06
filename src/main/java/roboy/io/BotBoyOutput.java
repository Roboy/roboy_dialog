package roboy.io;

import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;
import roboy.util.TelegramBotBoyPolling;

import java.util.List;

public class BotBoyOutput implements OutputDevice {
    private TelegramBotBoyPolling polling = TelegramBotBoyPolling.getInstance();

    @Override
    public void act(List<Action> actions) {

        for(Action a : actions){
            if(a instanceof SpeechAction){
                polling.sendMessage(((SpeechAction) a).getText());
            }
        }
    }
}
