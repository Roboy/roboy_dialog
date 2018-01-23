package roboy.context.visionContext;

import org.junit.Test;
import roboy.context.Context;
import roboy.context.InternalUpdater;
import roboy.context.contextObjects.CoordinateSet;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ContextTest {
    /**
     * Checks that the values of FACE_COORDINATES get automatically updated.
     */
    @Test
    public void getLastAttributeValue() throws Exception {
        int updateFrequency = 1; //Assuming the updater's frequency is 1 second!
        int sleeptime = updateFrequency * 1000 * 2; // Here in millis and double the actual update time.
        Context.Values face = Context.Values.FACE_COORDINATES;
        for(int i = 0; i < 3; i++) {
            CoordinateSet set = face.getLastValue();
            Thread.sleep(sleeptime);
            assertNotEquals(face.getLastValue(), set);
        }
    }

    @Test
    public void setAndGetDialogTopics() {
        Context ct = Context.getInstance();
        Context.Updaters updater = Context.Updaters.DIALOG_TOPICS_UPDATER;
        Context.ValueLists topics = Context.ValueLists.DIALOG_TOPICS;

        ct.updateValue(updater, "test1");
        assertEquals("test1", ( topics.getLastValue()));
        ct.updateValue(updater, "test2");
        Map<Integer, String> values = topics.getNLastValues(2);
        assertEquals("test1", values.get(0));
        assertEquals("test2", values.get(1));
    }
}