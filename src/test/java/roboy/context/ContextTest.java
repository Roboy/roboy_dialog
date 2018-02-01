package roboy.context;

import org.junit.Test;
import org.mockito.Mockito;
import roboy.context.contextObjects.CoordinateSet;
import roboy.context.contextObjects.FaceCoordinates;
import roboy.context.contextObjects.FaceCoordinatesObserver;
import roboy.memory.nodes.Interlocutor;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.atLeast;

public class ContextTest {

    @Test
    public void getLastAttributeValue() throws Exception {
        int updateFrequency = 1; //Assuming the updater's frequency is 1 second!
        int sleeptime = updateFrequency * 1000 * 2; // Here in millis and double the actual update time.
        Context.Values face = Context.Values.FACE_COORDINATES;
        for(int i = 0; i < 3; i++) {
            CoordinateSet set = face.getValue();
            Thread.sleep(sleeptime);
            assertNotEquals(face.getValue(), set);
        }
    }

    @Test
    public void setAndGetDialogTopics() {
        Context.InternalUpdaters updater = Context.InternalUpdaters.DIALOG_TOPICS_UPDATER;
        Context.ValueHistories topics = Context.ValueHistories.DIALOG_TOPICS;

        updater.updateValue("test1");
        assertEquals("test1", ( topics.getLastValue()));
        updater.updateValue("test2");
        Map<Integer, String> values = topics.getNLastValues(2);
        assertEquals("test1", values.get(0));
        assertEquals("test2", values.get(1));
    }

    @Test
    public void testInterlocutor() {
        Interlocutor in = Context.Values.ACTIVE_INTERLOCUTOR.getValue();
        assertNull(in);
        Interlocutor in2 = new Interlocutor();
        Context.InternalUpdaters.ACTIVE_INTERLOCUTOR_UPDATER.updateValue(in2);
        in = Context.Values.ACTIVE_INTERLOCUTOR.getValue();
        assertEquals(in, in2);
    }

    @Test
    public void testObserver() throws Exception {
        int updateFrequency = 1; //Assuming the updater's frequency is 1 second!
        int sleeptime = updateFrequency * 1000 * 2; // Here in millis and double the actual update time.

        FaceCoordinatesObserver observer = Mockito.spy(new FaceCoordinatesObserver());
        ((FaceCoordinates) Context.getInstance().values.get(Context.Values.FACE_COORDINATES.classType))
                .addObserver(observer);

        CoordinateSet value = Context.Values.FACE_COORDINATES.getValue();
        Thread.sleep(sleeptime);
        // Check that the value in FaceCoordinates was updated -> should trigger the observer.
        assertNotEquals("Face coordinates value should have been updated!",
                value, Context.Values.FACE_COORDINATES.getValue());
        Mockito.verify(observer, atLeast(1)).update(any(), any());
    }
}