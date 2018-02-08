package roboy.context;

import com.google.gson.Gson;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.ros.internal.message.RawMessage;
import org.ros.message.MessageListener;
import roboy.context.contextObjects.*;
import roboy.memory.nodes.Interlocutor;
import roboy.ros.RosMainNode;
import roboy_communication_cognition.DirectionVector;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.atLeast;

public class ContextTest {

    //@Test - does not work when external updaters are not initialized.
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

    //@Test - fails when external updaters are not initialized.
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

    @Test
    public void checkInternalUpdaterIntegrity() {
        // Check that internal updaters match target Value or ValueHistory.
        for (Context.InternalUpdaters updater : Context.InternalUpdaters.values()) {
            // Get the target class entry from Values or HistoryValues.
            for (Context.Values value : Context.Values.values()) {
                if (value.classType.equals(updater.targetType)) {
                    assertEquals(updater.targetValueType, value.returnType);
                    break;
                }
            }
            for (Context.ValueHistories history : Context.ValueHistories.values()) {
                if (history.classType.equals(updater.targetType)) {
                    assertEquals(updater.targetValueType, history.returnType);
                    break;
                }
            }
        }
    }

    @Test
    public void checkExternalUpdaterIntegrity() {
        // Check that internal updaters match target Value or ValueHistory.
        for(Context.ExternalUpdaters updater : Context.ExternalUpdaters.values()) {
            // Get the target class entry from Values or HistoryValues.
            for(Context.Values value : Context.Values.values()) {
                if(value.classType.equals(updater.targetType)) {
                    assertEquals(updater.targetValueType, value.returnType);
                    break;
                }
            }
            for(Context.ValueHistories history : Context.ValueHistories.values()) {
                if(history.classType.equals(updater.targetType)) {
                    assertEquals(updater.targetValueType, history.returnType);
                    break;
                }
            }
        }
    }

    @Test
    public void checkObserverIntegrity() {
        for(Context.Observers observer : Context.Observers.values()) {
            assertTrue(observer.targetType.getClass().isInstance(ObservableValue.class));
        }
    }

    @Test
    public void timestampedHistoryTest() {
        TimestampedValueHistory<String> testHistory = new TimestampedValueHistory<>();
        assertNull(testHistory.getValue());

        testHistory.updateValue("test1");
        Map<Long, String> values = testHistory.getLastNValues(2);
        assertEquals(1, values.size());
        assertEquals("test1", values.entrySet().iterator().next().getValue());

        testHistory.updateValue("test2");
        values = testHistory.getLastNValues(2);
        assertEquals(2, values.size());

        Iterator<Map.Entry<Long, String>> entries = values.entrySet().iterator();
        Map.Entry<Long, String> one = entries.next();
        Map.Entry<Long, String> two = entries.next();
        assertTrue(one.getValue().equals("test1") ||
            one.getKey() > two.getKey());
    }


    @Test
    public void audioAnglesTest() throws IOException {
        RosMainNode node = Mockito.mock(RosMainNode.class);
        ArgumentCaptor<MessageListener> argument = ArgumentCaptor.forClass(MessageListener.class);
        AudioDirection direction = new AudioDirection();
        AudioDirectionUpdater updater = new AudioDirectionUpdater(direction, node);
        Mockito.verify(node).addListener(argument.capture(), Mockito.any());
        Gson gson = new Gson();
        DirectionVector vector = gson.fromJson("{\"azimutal_angle\":0.5,\"polar_angle\":0.4}", DirVec.class);
        argument.getValue().onNewMessage(vector);
        assertNotNull(direction.getValue());
    }

    private class DirVec implements DirectionVector {
        double azimutal_angle;
        double polar_angle;

        @Override
        public double getAzimutalAngle() {
            return azimutal_angle;
        }

        @Override
        public void setAzimutalAngle(double v) {
            azimutal_angle = v;
        }

        @Override
        public double getPolarAngle() {
            return polar_angle;
        }

        @Override
        public void setPolarAngle(double v) {
            polar_angle = v;
        }

        @Override
        public RawMessage toRawMessage() {
            return null;
        }
    }
}