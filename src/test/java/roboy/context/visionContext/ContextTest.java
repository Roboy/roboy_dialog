package roboy.context.visionContext;

import org.junit.Test;
import roboy.context.Context;
import roboy.context.DirectUpdatePolicy;
import roboy.context.HistoryAttribute;
import roboy.context.dataTypes.CoordinateSet;
import roboy.context.dataTypes.DataType;
import roboy.context.dataTypes.Topic;
import roboy.context.dialogContext.DialogTopics;

import java.util.HashMap;
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
        int sleeptime = updateFrequency * 1000; // Here in millis.
        Context ct = Context.getInstance();
        Thread.sleep(sleeptime);
        for(int i = 0; i < 5; i++) {
            Context.ValueAttributes face = Context.ValueAttributes.FACE_COORDINATES;
            CoordinateSet set = face.getLastValue();
            Thread.sleep(sleeptime);
            assertNotEquals(face.getLastValue(), set);
        }
    }

    @Test
    public void setAndGetDialogTopics() {
        Context ct = Context.getInstance();
        DirectUpdatePolicy updater = ct.getUpdater(Context.Updaters.DIALOG_TOPICS_UPDATER);
        Context.HistoryAttributes topics = Context.HistoryAttributes.DIALOG_TOPICS;

        updater.putValue(new Topic("test1"));
        assertEquals("test1", ((Topic) topics.getLastValue()).topic);
        updater.putValue(new Topic("test2"));
        Map<Integer, Topic> values = topics.getNLastValues(2);
        assertEquals("test1", values.get(0).topic);
        assertEquals("test2", values.get(1).topic);
    }
}