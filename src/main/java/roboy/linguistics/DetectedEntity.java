package roboy.linguistics;

public class DetectedEntity {

	private Entity entity;
	private int tokenIndex;
	
	public DetectedEntity(Entity entity, int tokenIndex){
		this.entity = entity;
		this.tokenIndex = tokenIndex;
	}

	public Entity getEntity() {
		return entity;
	}

	public int getTokenIndex() {
		return tokenIndex;
	}

	
}
