package roboy.context.visionContext;

import org.junit.Test;
import roboy.context.Context;
import roboy.context.dataTypes.CoordinateSet;

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
            Context.HistoryAttributes face = Context.HistoryAttributes.FACE_COORDINATES;
            CoordinateSet set = face.getLastValue();
            Thread.sleep(sleeptime);
            assertNotEquals(face.getLastValue(), set);
        }
    }

}