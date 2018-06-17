package roboy.memory;

import java.util.*;

/**
 * A relation in the lexicon.
 */
public class LexiconPredicate implements Comparable<LexiconPredicate>
{

	public List<String> domains;
	public List<String> ranges;
	public String type;
	public String URI;
	public String label;
	public String QuestionMatch;
	// public String identifier ;
	public int score;

	public LexiconPredicate()
	{

		domains = new ArrayList<String>();
		ranges = new ArrayList<String>();
	}

	public LexiconPredicate(String URI, String Label)
	{
		this.URI = URI;
		this.label = Label;
	}

	public int compareTo(LexiconPredicate lexpre)
	{

		int score2 = ((LexiconPredicate) lexpre).score;

		// ascending order
		return this.score - score2;

	}

	public static Comparator<LexiconPredicate> scoreComparator = new Comparator<LexiconPredicate>()
	{
		public int compare(LexiconPredicate lexpre1, LexiconPredicate lexpre2)
		{

			return lexpre1.compareTo(lexpre2);
		}

	};
}
