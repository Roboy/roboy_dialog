package roboy.context.contextObjects;

import roboy.context.InternalUpdater;
import roboy.memory.nodes.Interlocutor;

public class ActiveInterlocutorUpdater extends InternalUpdater<ActiveInterlocutor, Interlocutor> {
    public ActiveInterlocutorUpdater(ActiveInterlocutor target) {
        super(target);
    }
}
