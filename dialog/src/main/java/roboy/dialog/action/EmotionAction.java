package roboy.dialog.action;

import roboy.emotions.RoboyEmotion;
import roboy.memory.nodes.Roboy;

/**
 * Action used if the dialogue manager wants Roboy to express a certain emotional expression,
 * like being angry, neutral or moving its lips (speak).
 */
public class EmotionAction implements Action
{
	
	private String state;
	private int duration;
	
	/**
	 * Constructor. Duration is set to 1.
	 * 
	 * @param state The emotional expression. Possible values: angry, neutral, speak
	 * @Deprecated Please use RoboyEmotions instead of state Strings.
	 */
	@Deprecated
	public EmotionAction(String state)
	{
		System.err.println("EmotionAction(String state) is deprecated. Please use EmotionAction(RoboyEmotion state) instead.");
		this.state = state;
		this.duration = 1;
	}

	/**
	 * Constructor. Duration is set to 1.
	 *
	 * @param state The emotional expression. Please refer roboy.emotions.RoboyEmotion for supported emotions.
	 */
	public EmotionAction(RoboyEmotion state)
	{
		this.state = state.type;
		this.duration = 1;
	}

	/**
	 * Constructor.
	 * 
	 * @param state The emotional expression. Possible values: angry, neutral, speak
	 * @param duration How long Roboy should display the given emotional expression
	 */
	public EmotionAction(String state, int duration)
	{
		this.state = state;
		this.duration = duration;
	}

	/**
	 * Constructor. Duration is set to 1.
	 *
     * @param state The emotional expression. Possible values: angry, neutral, speak
     * @param duration How long Roboy should display the given emotional expression
	 */
	public EmotionAction(RoboyEmotion state, int duration)
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