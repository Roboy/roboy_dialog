package roboy.linguistics.phonetics;

import org.apache.commons.codec.language.Soundex;

/**
 * A phonetic encoder using the method soundex that maps
 * words to their phonetic base form so that words that are written
 * differently but sound similar receive the same form.
 * 
 * This is intended to be used to correct terms that Roboy misunderstood,
 * but currently is not is use.
 */
public class SoundexEncoder implements PhoneticEncoder {
	
	private Soundex soundex;
	
	public SoundexEncoder (Soundex soundex) {
		this.soundex = soundex;
	}

	@Override
	public String encode(String input) {
		return soundex.encode(input);
	}
}
