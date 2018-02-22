package roboy.context;

import org.junit.Test;
import org.mockito.Mockito;
import roboy.context.contextObjects.CoordinateSet;
import roboy.context.contextObjects.FaceCoordinatesObserver;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;

public class ContextIntegrationTest {

    @Test
    public void getLastAttributeValue() throws Exception {
        int updateFrequency = 1; //Assuming the updater's frequency is 1 second!
        int sleeptime = updateFrequency * 1000 * 2; // Here in millis and double the actual update time.
        for(int i = 0; i < 3; i++) {
            Context.getInstance();
            CoordinateSet set = Context.FACE_COORDINATES.getValue();
            Thread.sleep(sleeptime);
            assertNotEquals("New face coordinates should have been added during idle time!",
                    Context.FACE_COORDINATES.getValue(), set);
        }
    }


    @Test
    public void testObserver() throws Exception {
        int updateFrequency = 1; //Assuming the updater's frequency is 1 second!
        int sleeptime = updateFrequency * 1000 * 2; // Here in millis and double the actual update time.

        FaceCoordinatesObserver observer = Mockito.spy(new FaceCoordinatesObserver());
        Context.FACE_COORDINATES.getContextObject().addObserver(observer);

        CoordinateSet value = Context.FACE_COORDINATES.getValue();
        Thread.sleep(sleeptime);
        // Check that the value in FaceCoordinates was updated -> should trigger the observer.
        assertNotEquals("Face coordinates value should have been updated!",
                value, Context.FACE_COORDINATES.getValue());
        Mockito.verify(observer, atLeast(1)).update(any(), any());
    }

}
