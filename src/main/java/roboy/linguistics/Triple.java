package roboy.linguistics;

import java.util.Objects;

/**
 * Represents a simple who(subject) does what(predicate) to whom(object) relation.
 */
public class Triple {
	
	public String subject;
	public String predicate;
	public String object;

	public Triple(String subject, String predicate, String object){
		this.predicate = predicate;
		this.subject = subject;
		this.object = object;
	}

    @Override
    public String toString() {
        return "Triple{" +
                "SUB: '" + subject + '\'' +
                ", PRED: '" + predicate + '\'' +
                ", OBJ: '" + object + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Triple comparableObject = (Triple) obj;
        return Objects.equals(subject, comparableObject.subject) &&
                Objects.equals(predicate, comparableObject.predicate) &&
                Objects.equals(this.object, comparableObject.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, predicate, object);
    }
}
