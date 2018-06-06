package roboy.context.contextObjects;

import roboy.context.AbstractValue;
import roboy.context.InternalUpdater;

/**
 * Updater available to all DM for adding new values to the DialogTopics attribute.
 */
public class OtherQuestionsUpdater extends InternalUpdater<AbstractValue<Integer>, Integer> {
    public OtherQuestionsUpdater(AbstractValue<Integer> target) {
        super(target);
    }
}
