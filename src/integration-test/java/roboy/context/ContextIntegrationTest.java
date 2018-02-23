package roboy.context;

import org.junit.BeforeClass;
import org.junit.Test;
import roboy.ros.RosMainNode;

import static org.junit.Assert.assertNotEquals;

public class ContextIntegrationTest {
    @BeforeClass
    public static void initializeWithROS() {
        RosMainNode ros = new RosMainNode();
        Context.getInstance().initializeROS(ros);
    }

    /**
     * For this test to work, start the TEST_TOPIC subscriber defined under RosSubscribers.
     */
    @Test
    public void getLastAttributeValue() throws Exception {
        int updateFrequency = 1; //Assuming the updater's frequency is 1 second!
        int sleeptime = updateFrequency * 1000 * 2; // Here in millis and double the actual update time.
        for(int i = 0; i < 3; i++) {
            int values = Context.ROS_TEST.valuesAddedSinceStart();
            Thread.sleep(sleeptime);
            assertNotEquals("New test values should have been added during idle time!",
                    Context.ROS_TEST.valuesAddedSinceStart(), values);
        }
    }

}
