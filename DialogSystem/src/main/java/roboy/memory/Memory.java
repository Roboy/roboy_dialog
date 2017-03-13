package roboy.memory;

import java.io.IOException;

public interface Memory {
	public boolean save() throws InterruptedException, IOException;
	public boolean retrieve() throws InterruptedException, IOException;
}