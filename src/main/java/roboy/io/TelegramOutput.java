package roboy.io;

import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;
import roboy.util.TelegramPolling;

import java.util.List;

public class TelegramOutput implements OutputDevice {

    private TelegramPolling polling = TelegramPolling.getInstance();
    private String uuid;

    public TelegramOutput(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public void act(List<Action> actions) {
        for(Action a : actions) {
            if (a instanceof SpeechAction) {
                String message = ((SpeechAction) a).getText();
                polling.sendMessage(message, uuid);
            }
        }
    }
}
