package roboy.context.contextObjects;

import roboy.context.InternalUpdater;
import roboy.memory.nodes.Interlocutor;

import java.util.Map;

public class ActiveInterlocutorsUpdater extends InternalUpdater<ActiveInterlocutors, Map<Integer, Interlocutor>> {
    public ActiveInterlocutorsUpdater(ActiveInterlocutors target) {
        super(target);
    }
}
