package roboy.io;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.util.TelegramBotBoyPolling;

public class BotBoyInput implements InputDevice  {
    private final static Logger logger = LogManager.getLogger();
    private TelegramBotBoyPolling polling = TelegramBotBoyPolling.getInstance();
    @Override
    public Input listen() throws InterruptedException, IOException {
        String messageFromTelegram = polling.getInput();
        //TODO: listener and event
        logger.error("Finally here!");

        return new Input(messageFromTelegram);
    }

}
