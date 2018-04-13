package roboy.linguistics;

/**
 * Represents a simple who(subject) does what(predicate) to whom(object) relation.
 */
public class Triple {
	
	public String subject; // subject
	public String predicate;
	public String object; // object

	public Triple(String predicate, String subject, String object){
		this.predicate = predicate;
		this.subject = subject;
		this.object = object;
	}
	
	public String toString(){
		return subject +"-"+predicate+"-"+ object;
	}
//	public boolean equals(Triple t)
//	{
//		return t.
//	}
}
