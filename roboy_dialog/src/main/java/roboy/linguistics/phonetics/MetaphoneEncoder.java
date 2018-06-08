package roboy.linguistics.phonetics;

import org.apache.commons.codec.language.Metaphone;

/**
 * A phonetic encoder using the method metaphone that maps
 * words to their phonetic base form so that words that are written
 * differently but sound similar receive the same form.
 * 
 * This is intended to be used to correct terms that Roboy misunderstood,
 * but currently is not is use.
 */
public class MetaphoneEncoder implements PhoneticEncoder{
	
	private Metaphone metaphone;
	
	public MetaphoneEncoder(Metaphone metaphone) {
		this.metaphone = metaphone;
	}

	@Override
	public String encode(String input) {
		return metaphone.encode(input);
	}

}
