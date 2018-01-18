package roboy.linguistics.phonetics;

import org.apache.commons.codec.language.DoubleMetaphone;

/**
 * A phonetic encoder using the method double metaphone that maps
 * words to their phonetic base form so that words that are written
 * differently but sound similar receive the same form.
 * 
 * This is intended to be used to correct terms that Roboy misunderstood,
 * but currently is not is use.
 */
public class DoubleMetaphoneEncoder implements PhoneticEncoder{
	
	DoubleMetaphone doubleMetaphone;
	
	public DoubleMetaphoneEncoder(DoubleMetaphone doubleMetaphone){
		this.doubleMetaphone = doubleMetaphone;
	}

	@Override
	public String encode(String input) {
		return doubleMetaphone.encode(input);
	}

}
