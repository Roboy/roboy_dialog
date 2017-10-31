package roboy.io;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import roboy.dialog.action.Action;

/**
 * Meta class to combine multiple output devices.
 */
public class MultiOutputDevice implements OutputDevice{
	
	private List<OutputDevice> devices;

	public MultiOutputDevice(OutputDevice... devices){
		this.devices = Arrays.asList(devices);
	}

	public void add(OutputDevice additionalDevice) {
		devices.add(additionalDevice);
	}
	
	@Override
	public void act(List<Action> actions) {
		for(OutputDevice device : devices){
			device.act(actions);
		}
	}

}
