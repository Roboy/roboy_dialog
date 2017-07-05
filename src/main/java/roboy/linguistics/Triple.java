package roboy.linguistics;

/**
 * Represents a simple who does what to whom relation.
 */
public class Triple {
	
	public String agens;
	public String predicate;
	public String patiens;

	public Triple(String predicate, String agens, String patiens){
		this.predicate = predicate;
		this.agens = agens;
		this.patiens = patiens;
	}
	
	public String toString(){
		return agens+"-"+predicate+"-"+patiens;
	}
//	public boolean equals(Triple t)
//	{
//		return t.
//	}
}
