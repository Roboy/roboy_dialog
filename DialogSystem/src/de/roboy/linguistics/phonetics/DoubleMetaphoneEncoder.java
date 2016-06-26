package de.roboy.linguistics.phonetics;

import org.apache.commons.codec.language.DoubleMetaphone;

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
