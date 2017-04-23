package roboy.io;

import java.util.List;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;

/**
 * Free TTS text to speech.
 */
public class FreeTTSOutput implements OutputDevice{

	private Voice voice;
	
	public FreeTTSOutput() {
		VoiceManager vm = VoiceManager.getInstance();
        voice = vm.getVoice("kevin16"); // kevin, kevin16, alan
	}
	@Override
	public void act(List<Action> actions) {
		for(Action a: actions){
			if(a instanceof SpeechAction){
		        voice.allocate();
		        voice.speak(((SpeechAction) a).getText());
			}
		}
	}
	
	public static void main(String[] args) {
		VoiceManager vm = VoiceManager.getInstance();
        Voice voice = vm.getVoice("kevin16");
        voice.allocate();
        voice.speak("Hello world");
        voice.speak("I am Roboy");
        voice.speak("How are you?");
	}

}