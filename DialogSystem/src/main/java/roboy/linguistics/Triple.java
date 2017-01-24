package roboy.linguistics;

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
}
