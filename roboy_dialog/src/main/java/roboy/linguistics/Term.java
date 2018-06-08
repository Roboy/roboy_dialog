package roboy.linguistics;

import java.util.List;

public class Term {
    private List<String> pos = null;
	private float probability = 0;
	private String concept = null;

    public List<String> getPos() {
        return pos;
    }

    public void setPos(List<String> pos) {
        this.pos = pos;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float prob) {
        this.probability = prob;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    @Override
    public String toString() {
        return "Term{" +
                "pos=" + pos +
                ", prob=" + probability +
                ", concept='" + concept + '\'' +
                '}';
    }
}
