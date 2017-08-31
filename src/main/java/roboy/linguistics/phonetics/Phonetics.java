package roboy.linguistics.phonetics;

import java.util.List;
import java.util.Map;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.Soundex;

@Deprecated
public class Phonetics {
	
	private Soundex soundex = new Soundex();
	private Map<String,List<String>> codecToWords;
	
	public List<String> similarWords(String word){
		String phoneticValue = soundex.encode(word);
		return codecToWords.get(phoneticValue);
	}

	public static void main(String[] args) {
		Soundex soundex = new Soundex();
		Metaphone metaphone = new Metaphone();
		DoubleMetaphone dmetaphone = new DoubleMetaphone();
		String[] words = new String[]{"tree","thee","try"};
		System.out.println(soundex.encode(words[0]));
		System.out.println(soundex.encode(words[1]));
		System.out.println(soundex.encode(words[2]));
		System.out.println(metaphone.encode(words[0]));
		System.out.println(metaphone.encode(words[1]));
		System.out.println(metaphone.encode(words[2]));
		System.out.println(dmetaphone.encode(words[0]));
		System.out.println(dmetaphone.encode(words[1]));
		System.out.println(dmetaphone.encode(words[2]));
	}
}
