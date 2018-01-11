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
        int updateFrequency = 1;
        int sleeptime = updateFrequency * 1000; // Here in millis. Assuming the update frequency is 1 second!
        Context ct = Context.getInstance();
        Thread.sleep(sleeptime);
        for(int i = 0; i < 5; i++) {
            Context.Attribute face = Context.Attribute.FACE_COORDINATES;
            CoordinateSet set = ct.getLastAttributeValue(face);
            Thread.sleep(sleeptime);
            assertNotEquals(ct.getLastAttributeValue(face), set);
        }
    }

}