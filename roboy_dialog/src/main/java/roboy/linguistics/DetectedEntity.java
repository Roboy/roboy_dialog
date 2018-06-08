package roboy.linguistics;

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
}
