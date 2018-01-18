package roboy.context.memoryContext;

import roboy.context.IntervalUpdatePolicy;

public class InterlocutorNodeUpdater extends IntervalUpdatePolicy<InterlocutorNode> {
    public InterlocutorNodeUpdater(InterlocutorNode target, int updateFrequencySeconds) {
        super(target, updateFrequencySeconds);
    }

    @Override
    protected void update() {
        //Needed only if Memory updating is not handled by the Interlocutor itself.
    }
}
