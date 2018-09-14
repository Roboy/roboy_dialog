package roboy.io;

import java.io.IOException;

/**
 * An input device must listen and return an Input object.
 */
public interface InputDevice {
	public Input listen() throws InterruptedException, IOException;

	default public Input listen(long timeout) throws InterruptedException, IOException {
		return this.listen();
	}
}
