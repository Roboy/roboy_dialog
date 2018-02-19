package roboy.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Meta class to combine multiple input devices.
 */
public class MultiInputDevice implements InputDevice{

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
			result.attributes.putAll(i.attributes);
		}
		return result;
	}

}
