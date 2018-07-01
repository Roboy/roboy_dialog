package roboy.io;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Meta class to combine multiple input devices.
 */
public class MultiInputDevice implements InputDevice, CleanUp{

	private InputDevice mainInput;
	private ArrayList<InputDevice> additionalInputs;
	
	public MultiInputDevice(InputDevice mainInput) {
		this.mainInput = mainInput;
		additionalInputs = new ArrayList<>();
	}

	public void addInputDevice(InputDevice additionalInput) {
		additionalInputs.add(additionalInput);
	}
	
	@Override
	public Input listen() throws InterruptedException, IOException {
		Input result = mainInput.listen();
		for(InputDevice device : additionalInputs){
			Input i = device.listen();
			result.setAttributes(i.getAttributes());
		}
		return result;
	}

	/**
	 * Calls cleanup() for every included input device that implements CleanUp.
	 */
	@Override
	public void cleanup() {

		//mainInput; if null, it probably has already been cleaned
		if(mainInput != null && mainInput instanceof CleanUp){
			((CleanUp) mainInput).cleanup();
			mainInput = null;
		}
		//additionalInputs; if an additionalInput has been cleaned it will be removed from additionalInputs
		for(InputDevice device : additionalInputs){
			if(device instanceof CleanUp) ((CleanUp) device).cleanup();
		}
		additionalInputs.clear();
	}

	@Override
	public void finalize(){//just in case someone forgot to clean their mess
		this.cleanup();
	}
}