package roboy.linguistics;

import java.util.List;

public class Term {

	public List<String> pos;
	public float prob;
	public String concept;
	
	public String toString(){
		return concept+" ("+prob+","+pos+")";
	}
}
