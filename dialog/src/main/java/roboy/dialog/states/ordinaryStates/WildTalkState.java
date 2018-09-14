package roboy.dialog.states.ordinaryStates;

import co.gongzh.procbridge.ProcBridge;
import co.gongzh.procbridge.ProcBridgeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.dialog.Segue;
import roboy.ros.RosMainNode;
import roboy.util.RandomList;

import java.util.Set;

import static roboy.util.ConfigManager.*;

/**
 * This fallback state will query the generative model over ROS to create a reply for any situation.
 *
 * This state is meant to be used as a fallback-only state. It only implements the react() function
 * returning a hardcoded random answer. This state should never become active (meaning that no transition
 * should point to it.)
 *
 * WildTalkState interface:
 * 1) Fallback is not required (this state should be the fallback).
 * 2) This state has no outgoing transitions.
 * 3) No parameters are used.
 */
public class WildTalkState extends State {

    private final Logger LOGGER = LogManager.getLogger();
    private ProcBridge pb;

    private RandomList<String> rosFailurePhrases = new RandomList<>(
            "Hey, who disconnected me from my beloved ros node? I need it! ",
            "Oh well, my generative model is not connected. That makes me sad. ",
            "Could you open a hotspot for me, I cannot connect to some services ",
            "I'm on holiday and don't have internet connection right now, let's talk about something else "
    );

    public WildTalkState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        pb = new ProcBridge(PARLAI_HOST, PARLAI_PORT, 2000);

    }

    @Override
    public Output act() {
        return Output.say("WildTalkState should never act!");
    }

    @Override
    public Output react(Interpretation input) {

        String sentence = input.getSentence();

        JSONObject resp;
        try {
            resp = pb.request("parlai", "{text_input: " + sentence + "}");
            LOGGER.info(resp);
            return Output.say(resp.getString("result"));
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage());
        }
        return Output.sayNothing();

    }

    @Override
    public State getNextState() {
        // no next state for fallback states
        return null;
    }



    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet();
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet();
    }

    @Override
    public boolean isFallbackRequired() {
        return false;
    }

}
