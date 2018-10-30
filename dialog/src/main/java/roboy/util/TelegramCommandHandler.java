package roboy.util;


import org.apache.jena.atlas.logging.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import java.util.concurrent.ThreadLocalRandom;

public class TelegramCommandHandler
{
    enum ExecutionType
    {
        SAY,
        BEER,
        UZUPHIS,
        PIC,
        NONE
    }

    String[] pictureUrls = new String[]{
            "https://b1.ro/pictures/2013/01/06/71670.jpg",
            "https://pbs.twimg.com/profile_images/378800000539737046/4c8c34ce24c4e634d708b34a82922ac4_400x400.jpeg",
            "http://roboy.org/wp-content/uploads/2017/05/roboy-1.png",
            "http://www.techaw.com/wp-content/uploads/2013/01/roboy-University-of-Zurich-Artificial-Intelligence-Lab.jpg",
            "https://roboy.org/wp-content/uploads/2018/04/Upper_1.jpg",
            "https://roboy.org/wp-content/uploads/2018/04/Roboy_head3.jpg",
            "https://roboy.org/wp-content/uploads/2018/04/P1055814-e1524420528347.jpg"
    };

    private static TelegramCommandHandler instance;

    private String beerResponses;
    private ExecutionType type;
    private UzupisIntents currentUzupisIntent;
    private final String additional;
    private final String telegramChatID;
    private final Logger LOGGER = LogManager.getLogger();

//    public static TelegramCommandHandler getInstance(){
//        if(instance == null){//for speed purposes
//            synchronized (TelegramCommandHandler.class){//in here: for threadsafety
//                //to prevent magic edgecases ruining our day
//                if(instance == null) instance = new TelegramCommandHandler();
//            }
//        }
//        return instance;
//    }

    public TelegramCommandHandler(String line, String id)
    {

        LOGGER.debug("Command has ben initialized");
        String[] parts = line.split(" ", 2);

        currentUzupisIntent = null;
        type = ExecutionType.NONE;

        if(parts[0].contains("say")) {
            type = ExecutionType.SAY;
        }
        else if(parts[0].contains("pic")) {
            type = ExecutionType.PIC;
        }
        else if(parts[0].contains("getbeer")) {
            type = ExecutionType.BEER;
        }
        else if(parts[0].contains("uzuphis")) {
            type = ExecutionType.UZUPHIS;
            currentUzupisIntent = UzupisIntents.INTRO;
        }

        // parts.length - 1 should be 1
        additional = parts[parts.length - 1];
        telegramChatID = id;
    }

    //dummy reactions to commands
    public void execute(){
        LOGGER.debug("Command is executed");
        if(type == ExecutionType.NONE){
            return;
        }

        TelegramCommunicationHandler tch = TelegramCommunicationHandler.getInstance();
        switch (type)
        {
            case PIC:
                //Just a roboy image, should be changed to desired roboy url
                int randomNum = ThreadLocalRandom.current().nextInt(0, pictureUrls.length);
                String url = pictureUrls[randomNum];
                tch.sendImageFromUrl(url, telegramChatID);
                break;
            case SAY:
                //It should send a sound not the exact words
                tch.sendMessage(additional, telegramChatID);
                break;
            case BEER:
                //just a dummy response of get beer command
                String beerResponse = "Did you really take a drink? This will cost me 1€!\n";
                beerResponse += "All right, no biggie! Let me know next time!";
                tch.sendMessage(beerResponse, telegramChatID);
                break;
            case UZUPHIS:
//                TelegramEmotionInfluencer a = TelegramEmotionInfluencer.getInstance();
//                String nearest = a.getInfluencer(additional);
//                if(nearest == null)
//                    nearest = "[NULL]";
//
//                String message = "the result: " + nearest;
                String message = "the result: []";
                tch.sendMessage(message, telegramChatID);
                break;
            case NONE:
                break;
        }
    }
}
