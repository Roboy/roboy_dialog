package roboy.io;

import java.io.IOException;

import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Maps;

/**
 * Should perform the celebrity look-a-like vison input. Isn't implemented yet.
 */
public class CelebritySimilarityInput implements InputDevice{

	@Override
	public Input listen() {
		Interpretation interpretation = new Interpretation();
		interpretation.setCelebrity("Sponge Bob");
		return new Input(null, interpretation);
	}
}
