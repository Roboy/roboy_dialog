package org.roboy.io;

import org.roboy.linguistics.sentenceanalysis.Interpretation;

/**
 * Should perform the celebrity look-a-like vison input. Isn't implemented yet.
 */
public class CelebritySimilarityInput implements InputDevice {

	@Override
	public Input listen() {
		Interpretation interpretation = new Interpretation();
		interpretation.setCelebrity("Sponge Bob");
		return new Input(null, interpretation);
	}
}
