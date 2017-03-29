package roboy.util;

import roboy.util.Concept;

public class Relation {
	
	private Concept subject;
	private Concept object;
	private String predicate;
	
	public Relation(Concept subject, String predicate, Concept object){
		this.subject = subject;
		this.object = object;
		this.predicate = predicate;
	}

}
