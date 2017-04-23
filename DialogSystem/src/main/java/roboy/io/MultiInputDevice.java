package roboy.io;

import java.io.IOException;

/**
 * Meta class to combine multiple input devices.
 */
public class MultiInputDevice implements InputDevice{

	private InputDevice mainInput;
	private InputDevice[] additionalInputs;
	
	public MultiInputDevice(InputDevice mainInput, InputDevice... additionalInputs) {
		this.mainInput = mainInput;
		this.additionalInputs = additionalInputs;
	}
	
	@Override
	public Input listen() throws InterruptedException, IOException {
		Input result = mainInput.listen();
		if(additionalInputs!=null){
			for(InputDevice device : additionalInputs){
				Input i = device.listen();
				result.attributes.putAll(i.attributes);
			}
		}
//		System.out.println(result.attributes);
		return result;
	}

}
