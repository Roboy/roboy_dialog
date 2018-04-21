package roboy.dialog.states.expoStates;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.RandomList;

import java.util.Arrays;
import java.util.Set;


/**
 * This state asks the interlocutor whether Roboy should demonstrate one of his abilities.
 * The abilities include: shake hand, recognize objects, show emotion, move body
 *
 * Every time this state is entered, Roboy picks one of the abilities (that haven't been demonstrated yet)
 * and asks the interlocutor whether it should be demonstrated. If all abilities were already demonstrated,
 * one is chosen at random.
 *
 * The ability is demonstrated only if the interlocutor said yes (or similar). In this case, the
 * 'abilityWasDemonstrated' transition is used. Otherwise, there is no demonstration and the
 * 'abilityDemonstrationSkipped' transition is taken.
 *
 * DemonstrateAbilitiesState interface:
 * 1) Fallback is not required.
 * 2) Outgoing transitions that have to be defined:
 *    - abilityWasDemonstrated:    following state if the ability was demonstrated
 *    - abilityDemonstrationSkipped:            following state if the ability demonstration was skipped
 * 3) No parameters are used.
 */
public class DemonstrateAbilitiesState extends State {

    public enum RoboyAbility {
        SHAKE_HAND,
        RECOGNIZE_OBJECTS,
        SHOW_EMOTION,
        MOVE_BODY
    }


    private final static String TRANS_ABILITY_DEMONSTRATED = "abilityWasDemonstrated";
    private final static String TRANS_ABILITY_SKIPPED = "abilityDemonstrationSkipped";

    private final RandomList<RoboyAbility> availableAbilities = new RandomList<>();
    private RoboyAbility activeAbility;

    public DemonstrateAbilitiesState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        resetAvailableAbilities();
    }

    @Override
    public Output act() {
        activeAbility = selectRandomAbility();
        return null;
    }

    @Override
    public Output react(Interpretation input) {
        return null;
    }

    @Override
    public State getNextState() {
        return null;
    }


    /**
     * Resets the list of available abilities so that it contains all of them.
     */
    private void resetAvailableAbilities() {
        availableAbilities.clear();
        availableAbilities.addAll(Arrays.asList(RoboyAbility.values()));
    }

    /**
     * Selects one of the abilities from the availableAbilities list at random and removes it from the list.
     * If the list becomes empty this way, resets it to the initial state
     * @return one of the available abilities
     */
    private RoboyAbility selectRandomAbility() {
        RoboyAbility ability = availableAbilities.getRandomElement();
        availableAbilities.remove(ability);
        if (availableAbilities.size() == 0) {
            resetAvailableAbilities(); // reset if all abilities were used
        }
        return ability;
    }




    //////////  STATE INTERFACE

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(TRANS_ABILITY_DEMONSTRATED, TRANS_ABILITY_SKIPPED);
    }
}
