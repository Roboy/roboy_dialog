package roboy.io;

import java.io.*;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.AudioFormat;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.ibm.watson.developer_cloud.text_to_speech.v1.util.WaveUtils;

import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;

import roboy.util.ConfigManager;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;


/**
 * Uses IBM Cloud text to speech. Requires internet connection and valid IBM Bluemix credentials.
 */
public class IBMWatsonOutput implements OutputDevice{

    private TextToSpeech synthesizer;
    final Logger logger = LogManager.getLogger();

    public IBMWatsonOutput() {
        synthesizer = new TextToSpeech();
        synthesizer.setUsernameAndPassword(ConfigManager.IBM_TTS_USER, ConfigManager.IBM_TTS_PASS);
    }

    @Override
    public void act(List<Action> actions) {
        for(Action a : actions){
            if(a instanceof SpeechAction){
                say(((SpeechAction) a).getText());
            }
        }
    }

    public void say(String text)
    {

        // add child voice
        text = "<voice-transformation type=\"Young\" strength=\"10%\">" + text + "</voice-transformation>";
        //synthesize
        InputStream in = synthesizer.synthesize(text, Voice.EN_ALLISON, AudioFormat.WAV).execute();

        //play
        try {
            in = WaveUtils.reWriteWaveHeader(in);
            AudioStream ain = new AudioStream(in);
            AudioPlayer.player.start(ain);
            AudioPlayer.player.join( (long) (0.025*ain.getLength() + 100));
            logger.info("done talking");
//            AudioPlayer.player.wait();
        }catch (Exception e) {
            logger.error("Unable to synthesize speech");
            logger.error(e.getMessage());
        }

    }
}

