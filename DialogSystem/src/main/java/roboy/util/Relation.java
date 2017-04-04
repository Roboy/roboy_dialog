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

	public String getSubject()
	{
		return (String) subject.getAttribute("name");
	}
	public String getObject()
	{
		return (String) object.getAttribute("name");
	}

}
