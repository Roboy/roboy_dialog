package roboy.context.memoryContext;

import roboy.context.IntervalUpdatePolicy;

public class InterlocutorNodeUpdater extends IntervalUpdatePolicy<InterlocutorNode> {
    public InterlocutorNodeUpdater(InterlocutorNode target, int updateFrequencySeconds) {
        super(target, updateFrequencySeconds);
    }

    @Override
    protected void update() {
        // Empty for now as the updating functionality needs to be removed from the Interlocutor itself.
        // TODO Remove memory updating from Interlocutor, instead Context updates it at regular intervals.
    }
}
