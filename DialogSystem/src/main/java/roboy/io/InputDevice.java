package roboy.io;

import java.io.IOException;

public interface InputDevice {
	public Input listen() throws InterruptedException, IOException;
}
