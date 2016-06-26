package de.roboy.linguistics.phonetics;

import org.apache.commons.codec.language.Metaphone;

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
