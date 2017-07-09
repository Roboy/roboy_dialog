package roboy.util.ros;

import org.junit.Test;
import roboy.dialog.personality.states.WildTalkState;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.RosMainNode;

import java.util.concurrent.TimeUnit;

/**
 * Created by roboy on 7/9/17.
 */
public class RosCommunicationTest {
    @Test
    public void TestGenerativeModel()
    {
        RosMainNode.getInstance();
        try {
            TimeUnit.SECONDS.sleep(3);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        WildTalkState s = new WildTalkState();
        System.out.print(s.react(new Interpretation("wazuup")).getReactions().get(0).toString());
    }
}
