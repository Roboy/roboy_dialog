package roboy.io;

import java.util.List;

import roboy.dialog.action.Action;

public class MultiOutputDevice implements OutputDevice{
	
	private OutputDevice[] devices;

	public MultiOutputDevice(OutputDevice...devices){
		this.devices = devices;
	}
	
	@Override
	public void act(List<Action> actions) {
		for(OutputDevice device : devices){
			device.act(actions);
		}
	}

}
