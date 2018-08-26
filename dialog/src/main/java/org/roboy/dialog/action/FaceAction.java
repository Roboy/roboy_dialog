package org.roboy.dialog.action;

import org.roboy.emotions.RoboyEmotion;
import roboy.emotions.RoboyEmotion;

/**
 * Action used if the dialogue manager wants RoboyModel to express a certain facial expression,
 * like being angry, neutral or moving its lips (speak).
 */
public class FaceAction implements Action
{
	
	private String state;
	private int duration;
	
	/**
	 * Constructor. Duration is set to 1.
	 * 
	 * @param state The facial expression. Possible values: angry, neutral, speak
	 */
	public FaceAction(String state)
	{
		this.state = state;
		this.duration = 1;
	}

	/**
	 * Constructor. Duration is set to 1.
	 *
	 * @param state The facial expression. Possible values: angry, neutral, speak
	 */
	public FaceAction(RoboyEmotion state)
	{
		this.state = state.type;
		this.duration = 1;
	}

	/**
	 * Constructor.
	 * 
	 * @param state The facial expression. Possible values: angry, neutral, speak
	 * @param duration How long RoboyModel should display the given facial expression
	 */
	public FaceAction(String state, int duration)
	{
		this.state = state;
		this.duration = duration;
	}

	/**
	 * Constructor. Duration is set to 1.
	 *
     * @param state The facial expression. Possible values: angry, neutral, speak
     * @param duration How long RoboyModel should display the given facial expression
	 */
	public FaceAction(RoboyEmotion state, int duration)
	{
		this.state = state.type;
		this.duration = duration;
	}

	public String getState()
	{
		return this.state;
	}

	public int getDuration()
	{
		return this.duration;
	}

}