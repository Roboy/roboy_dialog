package roboy.context.memoryContext;

import roboy.memory.nodes.Interlocutor;

/**
 * Stores the Memory node of Interlocutor.
 * Do not store a reference to the instance for reuse over multiple actions.
 */
public class InterlocutorNode {
    private Interlocutor interlocutor;

    public InterlocutorNode() {
    }

    public Interlocutor getInterlocutor() {
        return interlocutor;
    }
}
