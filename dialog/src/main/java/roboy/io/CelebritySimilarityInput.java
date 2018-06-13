package roboy.io;

import java.io.IOException;

import roboy.linguistics.Linguistics;
import roboy.util.Maps;

/**
 * Should perform the celebrity look-a-like vison input. Isn't implemented yet.
 */
public class CelebritySimilarityInput implements InputDevice{

	@Override
	public Input listen() throws InterruptedException, IOException {
		return new Input(null, Maps.stringObjectMap("celebrity", "Sponge Bob"));
	}
}
