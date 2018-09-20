package roboy.io;

import roboy.dialog.action.Action;
import roboy.dialog.action.SoundAction;
import roboy.ros.RosMainNode;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Cerevoice text to speech
 */
public class SoundOutput implements OutputDevice
{

	private RosMainNode rosMainNode;

	public SoundOutput(RosMainNode node){
		this.rosMainNode = node;
	}

	private ReentrantLock lock = new ReentrantLock();

	@Override
	public void act(List<Action> actions) {
		for(Action a : actions){
			if(a instanceof SoundAction){
				final String filename = ((SoundAction) a).getFilename();
				lock.lock();
				play(filename);
				lock.unlock();
			}
		}
	}
	
	public void play(String filename)
	{
		rosMainNode.PlaySoundFile(filename);
	}

}
