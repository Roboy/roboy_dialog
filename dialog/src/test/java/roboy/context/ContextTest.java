package roboy.context;

import com.google.gson.Gson;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.ros.internal.message.RawMessage;
import org.ros.message.MessageListener;
import roboy.context.contextObjects.*;
import roboy.memory.DummyMemory;
import roboy.memory.Neo4jRelationship;
import roboy.memory.nodes.Interlocutor;
import roboy.ros.RosMainNode;
import roboy_communication_cognition.DirectionVector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

public class ContextTest {
    Context context = new Context();

    @Test
    public void setAndGetDialogTopics() {
        DialogTopicsUpdater updater = context.DIALOG_TOPICS_UPDATER;
        HistoryInterface<DialogTopics, Integer, String> topics = context.DIALOG_TOPICS;

        updater.updateValue("test1");
        assertEquals("test1", (topics.getLastValue()));
        updater.updateValue("test2");
        Map<Integer, String> values = topics.getLastNValues(2);
        assertEquals("test1", values.get(0));
        assertEquals("test2", values.get(1));
    }

    @Test
    public void setAndGetDialogIntents() {
        DialogIntentsUpdater updater = context.DIALOG_INTENTS_UPDATER;
        HistoryInterface<DialogIntents, Integer, IntentValue> intents = context.DIALOG_INTENTS;

        updater.updateValue(new IntentValue("test_id1", Neo4jRelationship.FROM));
        IntentValue testIntent = intents.getLastValue();
        assertEquals("test_id1", testIntent.getId());
        assertEquals(Neo4jRelationship.FROM, (testIntent.getNeo4jRelationshipValue()));
        updater.updateValue(new IntentValue("test_id2", Neo4jRelationship.HAS_HOBBY));
        Map<Integer, IntentValue> values = intents.getLastNValues(2);
        assertEquals("test_id1", values.get(0).getId());
        assertEquals("test_id2", values.get(1).getId());
    }

    @Test
    public void testInterlocutor() {
        Interlocutor in = context.ACTIVE_INTERLOCUTOR.getValue();
        assertNull(in);
        Interlocutor in2 = new Interlocutor(new DummyMemory());
        context.ACTIVE_INTERLOCUTOR_UPDATER.updateValue(in2);
        in = context.ACTIVE_INTERLOCUTOR.getValue();
        assertEquals("Should return the last added Interlocutor instance!", in, in2);
    }

    @Test
    public void timestampedHistoryTest() {
        TimestampedValueHistory<String> testHistory = new TimestampedValueHistory<>();
        assertNull(testHistory.getValue());

        testHistory.updateValue("test1");
        TreeMap<Long, String> values = testHistory.getLastNValues(2);
        assertEquals(1, values.size());
        assertEquals("test1", values.entrySet().iterator().next().getValue());

        testHistory.updateValue("test2");
        values = testHistory.getLastNValues(2);
        assertEquals(2, values.size());
        Iterator<Long> returnedKeys = values.keySet().iterator();
        Long key1 = returnedKeys.next();
        Long key2 = returnedKeys.next();
        assertTrue("Keys of the timestamped history were in reverse order!", key1 < key2);
        assertTrue("Second added value should have a higher key!", values.get(key2).equals("test2"));
    }

    @Test
    public void historyLimitExceededTest() {
        ValueHistory<String> testHistory = new ValueHistory<>();

        int limit = testHistory.getMaxLimit(); // Standard is 50, but could change in the future.
        for(int i = 0; i < limit + 100; i++) {
            testHistory.updateValue("test"+i);
        }
        HashMap<Integer, String> historyValues = testHistory.getLastNValues(limit + 1);

        assertEquals(limit, historyValues.size());
        for(int i = 0; i < limit; i++) {
            assertTrue(historyValues.containsKey(i));
        }
    }

    @Test
    public void timestampedHistoryLimitExceededTest() {
        TimestampedValueHistory<String> testTimestampHistory = new TimestampedValueHistory<>();
        int limit = testTimestampHistory.getMaxLimit(); // Standard is 50, but could change in the future.
        for(int i = 0; i < limit + 100; i++) {
            testTimestampHistory.updateValue("test"+i);
        }
        TreeMap<Long, String> timestampHistoryValues = testTimestampHistory.getLastNValues(limit + 1);

        assertEquals(limit, timestampHistoryValues.size());
    }

    @Test
    public void testObserver() throws Exception {
        int updateFrequency = 1; //Assuming the updater's frequency is 1 second!
        int sleeptime = updateFrequency * 1000 * 2; // Here in millis and double the actual update time.
        FaceCoordinatesObserver observer = Mockito.spy(new FaceCoordinatesObserver());
        context.FACE_COORDINATES.getContextObject().addObserver(observer);
        context.FACE_COORDINATES.getContextObject().updateValue(new CoordinateSet(0,0,0));
        Thread.sleep(sleeptime);
        // Check that the value in FaceCoordinates was updated -> should trigger the observer.
        Mockito.verify(observer, Mockito.atLeast(1)).update(any(), any());
    }

    @Test
    public void audioDirectionsTest() {
        // Get the subscriber for AudioDirection.
        RosMainNode node = Mockito.mock(RosMainNode.class);
        ArgumentCaptor<MessageListener> argument = ArgumentCaptor.forClass(MessageListener.class);
        AudioDirection direction = new AudioDirection();
        AudioDirectionUpdater updater = new AudioDirectionUpdater(direction, node);
        Mockito.verify(node).addListener(argument.capture(), Mockito.any());
        // Send value to subscriber and check that it was stored in the ValueHistory.
        Gson gson = new Gson();
        DirectionVector vector = gson.fromJson("{\"azimutal_angle\":0.5,\"polar_angle\":0.4}", DirVec.class);
        argument.getValue().onNewMessage(vector);
        assertNotNull("Audio directions were not added to the value history!", direction.getValue());
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