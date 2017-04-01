package roboy.dialog.action;

public class FaceAction implements Action
{

	private String state;
	private double duration; 
	
	public FaceAction(String state)
	{
		this.state = state;
		this.duration = 1;
	}

	public FaceAction(String state, double duration)
	{
		this.state = state;
		this.duration = duration;
	}

	public String getState()
	{
		return this.state;
	}

	public double getDuration()
	{
		return this.duration;
	}

}