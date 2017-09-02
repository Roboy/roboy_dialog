package roboy.linguistics.phonetics;

/**
 * An interface for phonetic encoders that map
 * words to their phonetic base form so that words that are written
 * differently but sound similar receive the same form.
 * 
 * This is intended to be used to correct terms that Roboy misunderstood,
 * but currently is not is use.
 */
public interface PhoneticEncoder {

	public String encode(String input);
}
