package roboy.memory;

import java.util.*;
import java.util.Comparator;

/**
 * An entity in the lexicon.
 */
public class LexiconLiteral implements Comparable<LexiconLiteral>
{
	
	public List<String> typeOfOwner;
	public String URI;
	public String label;
	public String QuestionMatch;
	public int score;

	public LexiconLiteral() {

		typeOfOwner = new ArrayList<String>();
	}

	public LexiconLiteral(String URI, String label, String QuestionMatch, List<String> typeOfOwnerList) {
		this.URI = URI;
		this.label = label;
		this.QuestionMatch = QuestionMatch;
		this.typeOfOwner = typeOfOwnerList;
	}

	public LexiconLiteral(String URI, String label, String QuestionMatch, String typeOfOwner) {
		this.URI = URI;
		this.label = label;
		this.QuestionMatch = QuestionMatch;
		List<String> typeOfOwnerList = new ArrayList<String>();
		typeOfOwnerList.add(typeOfOwner);
		this.typeOfOwner = typeOfOwnerList;
	}

	public int compareTo(LexiconLiteral lexlit) {
		int score2 = ((LexiconLiteral) lexlit).score;
		return this.score - score2;
	}

	public static Comparator<LexiconLiteral> scoreComparator = new Comparator<LexiconLiteral>() {

		public int compare(LexiconLiteral lexlit1, LexiconLiteral lexlit2) {
			return lexlit1.compareTo(lexlit2);
		}

	};

}
