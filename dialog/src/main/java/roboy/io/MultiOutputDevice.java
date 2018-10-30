package roboy.io;

import java.util.ArrayList;
import java.util.List;

import roboy.dialog.action.Action;

/**
 * Meta class to combine multiple output devices.
 */
public class MultiOutputDevice implements OutputDevice, CleanUp{
	
	private ArrayList<OutputDevice> devices;

	public MultiOutputDevice(OutputDevice device){
		this.devices = new ArrayList<>();
		devices.add(device);
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

	/**
	 * Calls cleanup for all devices and removes them from the devices list after cleaning
	 */
	@Override
	public void cleanup() {
		//if a device has been cleaned it will be removed from devices
		for(OutputDevice device : devices){
			if(device instanceof CleanUp) ((CleanUp) device).cleanup();
		}
		devices.clear();
	}

	@Override
	protected void finalize(){//just in case someone forgot to clean their mess
		this.cleanup();
	}
}