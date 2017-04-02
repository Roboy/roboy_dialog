package roboy.io;

import java.io.IOException;

import roboy.linguistics.Linguistics;
import roboy.util.Maps;

public class CelebritySimilarityInput implements InputDevice{

	@Override
	public Input listen() throws InterruptedException, IOException {
		return new Input(null,Maps.stringObjectMap(Linguistics.CELEBRITY,"Sponge Bob"));
	}

}
