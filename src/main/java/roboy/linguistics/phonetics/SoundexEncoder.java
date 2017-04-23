package roboy.linguistics.phonetics;

import org.apache.commons.codec.language.Soundex;

public class SoundexEncoder implements PhoneticEncoder{
	
	private Soundex soundex;
	
	public SoundexEncoder(Soundex soundex) {
		this.soundex = soundex;
	}

	@Override
	public String encode(String input) {
		return soundex.encode(input);
	}

	
}
