package roboy.linguistics;

/**
 * Represents a simple who(subject) does what(predicate) to whom(object) relation.
 */
public class Triple {
	
	public String subject;
	public String predicate;
	public String object;

	public Triple(String predicate, String subject, String object){
		this.predicate = predicate;
		this.subject = subject;
		this.object = object;
	}
	
	public String toString(){
		return subject +"-"+predicate+"-"+ object;
	}

	public boolean equals(Object obj)
	{
        if (!(obj instanceof Triple)) {
            return false;
        }

        boolean[] equality = {false, false, false};
        Triple comparableObject = (Triple) obj;

        if (subject != null) {
            equality[0] = comparableObject.subject.equals(this.subject);
        }

        if (predicate != null) {
            equality[1] = comparableObject.predicate.equals(this.predicate);
        }

        if (object != null) {
            equality[2] = comparableObject.object.equals(this.object);
        }

        return equality[0] && equality[1] && equality[2];
	}
}
