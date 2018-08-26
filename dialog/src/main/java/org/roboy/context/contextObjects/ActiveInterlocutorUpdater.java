package org.roboy.context.contextObjects;

import roboy.context.InternalUpdater;
import org.roboy.memory.models.nodes.Interlocutor;

/**
 * The interface for DM to replace the InterlocutorModel value held in the target ActiveInterlocutor instance.
 */
public class ActiveInterlocutorUpdater extends InternalUpdater<ActiveInterlocutor, Interlocutor> {
    public ActiveInterlocutorUpdater(ActiveInterlocutor target) {
        super(target);
    }
}
