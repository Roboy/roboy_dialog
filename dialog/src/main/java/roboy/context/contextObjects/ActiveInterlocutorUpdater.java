package roboy.context.contextObjects;

import roboy.context.InternalUpdater;
import roboy.memory.nodes.Interlocutor;

/**
 * The interface for DM to replace the Interlocutor value held in the target ActiveInterlocutor instance.
 */
public class ActiveInterlocutorUpdater extends InternalUpdater<ActiveInterlocutor, Interlocutor> {
    public ActiveInterlocutorUpdater(ActiveInterlocutor target) {
        super(target);
    }
}
