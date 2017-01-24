package roboy.io;

import java.io.IOException;

public interface InputDevice {
	public String listen() throws InterruptedException, IOException;
}
