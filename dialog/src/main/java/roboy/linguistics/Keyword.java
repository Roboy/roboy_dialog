package roboy.linguistics;

import java.util.Objects;


public class Keyword {
	private String keyword;
	private double score;
	
	public Keyword(double score, String keyword){
		this.score = score;
		this.keyword = keyword;
	}

	public String getKeyword() {
		return keyword;
	}

	public double getScore() {
		return score;
	}

    @Override
    public String toString() {
        return "Keyword{" +
                "keyword=" + keyword +
                ", score=" + score +
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

		Keyword comparableObject = (Keyword) obj;
		return getScore() == comparableObject.getScore() &&
				Objects.equals(getKeyword(), comparableObject.getKeyword());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getKeyword(), getScore());
	}
}
