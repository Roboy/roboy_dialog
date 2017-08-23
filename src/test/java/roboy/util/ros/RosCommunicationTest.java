package roboy.util.ros;

import org.junit.Test;
import roboy.dialog.personality.states.WildTalkState;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.ros.RosMainNode;

import java.util.concurrent.TimeUnit;

/**
 * Created by roboy on 7/9/17.
 */
public class RosCommunicationTest {
    @Test
    public void TestGenerativeModel()
    {
        RosMainNode node = new RosMainNode();
        try {
            TimeUnit.SECONDS.sleep(3);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        WildTalkState s = new WildTalkState(node);
        System.out.print(s.react(new Interpretation("wazuup")).getReactions().get(0).toString());
    }
}
