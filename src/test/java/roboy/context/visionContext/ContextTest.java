package roboy.context.visionContext;

import org.junit.Test;
import roboy.context.Context;
import roboy.context.contextObjects.CoordinateSet;
import roboy.memory.nodes.Interlocutor;

import java.util.Map;

import static org.junit.Assert.*;

public class ContextTest {
    /**
     * Checks that the values of FACE_COORDINATES get automatically updated.
     */
    @Test
    public void getLastAttributeValue() throws Exception {
        int updateFrequency = 1; //Assuming the updater's frequency is 1 second!
        int sleeptime = updateFrequency * 1000 * 2; // Here in millis and double the actual update time.
        Context.Value face = Context.Value.FACE_COORDINATES;
        for(int i = 0; i < 3; i++) {
            CoordinateSet set = face.getValue();
            Thread.sleep(sleeptime);
            assertNotEquals(face.getValue(), set);
        }
    }

    @Test
    public void setAndGetDialogTopics() {
        Context.InternalUpdater updater = Context.InternalUpdater.DIALOG_TOPICS_UPDATER;
        Context.ValueHistory topics = Context.ValueHistory.DIALOG_TOPICS;

        updater.updateValue("test1");
        assertEquals("test1", ( topics.getLastValue()));
        updater.updateValue("test2");
        Map<Integer, String> values = topics.getNLastValues(2);
        assertEquals("test1", values.get(0));
        assertEquals("test2", values.get(1));
    }

    @Test
    public void testInterlocutor() {
        Interlocutor in = Context.Value.ACTIVE_INTERLOCUTOR.getValue();
        assertNull(in);
        Interlocutor in2 = new Interlocutor();
        Context.InternalUpdater.ACTIVE_INTERLOCUTOR_UPDATER.updateValue(in2);
        in = Context.Value.ACTIVE_INTERLOCUTOR.getValue();
        assertEquals(in, in2);
    }
}