package roboy.context;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Ignore;
import roboy.ros.RosMainNode;

import static org.junit.Assert.assertNotEquals;


@Deprecated
public class ContextIntegrationTest {
    private RosMainNode ros;
    private Context context;

    @Before
    public void initializeRosAndContext(){
        ros = new RosMainNode();
        context = new Context();
        context.initializeROS(ros);
    }

    /**
     * This test tests if the context is properly integrated into the external (ROS) environment.
     * For this test to work, start the TEST_TOPIC subscriber defined under RosSubscribers via ROS_ACTIVE_PKGS: roboy_test in config.properties
     *
     * Also, publishing needs to be done externally (e.g. manually via [rostopic pub -r 1 /roboy std_msgs/String "data: 'Test'"]).
     */
    @Ignore
    @Test
    public void checkROSTopicUpdating() throws Exception {
        int updateFrequency = 1; //Assuming the updater's frequency is 1 second!
        int sleeptime = updateFrequency * 1000 * 2; // Here in millis and double the actual update time.
        for(int i = 0; i < 3; i++) {
            int values = context.ROS_TEST.valuesAddedSinceStart();
            Thread.sleep(sleeptime);
            assertNotEquals("New test values should have been added during idle time!",
                    context.ROS_TEST.valuesAddedSinceStart(), values);
        }
    }

}