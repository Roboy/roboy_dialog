package roboy.linguistics;

import java.util.Objects;


public class DetectedEntity {
	private Entity entity;
	private int tokenIndex;
	
	public DetectedEntity(int tokenIndex, Entity entity){
		this.tokenIndex = tokenIndex;
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

	public int getTokenIndex() {
		return tokenIndex;
	}

    @Override
    public String toString() {
        return "DetectedEntity{" +
                "entity=" + entity +
                ", tokenIndex=" + tokenIndex +
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

		DetectedEntity comparableObject = (DetectedEntity) obj;
		return getTokenIndex() == comparableObject.getTokenIndex() &&
				Objects.equals(getEntity(), comparableObject.getEntity());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getEntity(), getTokenIndex());
	}
}
