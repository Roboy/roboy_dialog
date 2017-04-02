package roboy.util;

import roboy.util.Concept;

public class Relation {
	
	public Concept subject;
	public Concept object;
	public String predicate;
	
	public Relation(Concept subject, String predicate, Concept object){
		this.subject = subject;
		this.object = object;
		this.predicate = predicate;
	}

}
