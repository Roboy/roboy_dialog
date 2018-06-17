package roboy.dialog;


import roboy.talk.PhraseCollection;
import roboy.util.RandomList;

/**
 * A segue /ˈsɛɡweɪ/ is a smooth transition from one topic to the next.  (c) Wikipedia
 *
 * Dialog states can decide to add segues to their output (in act() or react()) to improve the dialog flow.
 * Segues are categorized by types and will be inserted into the conversation with a certain probability.
 *
 * Special options:
 *  - "%s" inside a segue will be replaced with interlocutor's name if available.
 *    If no interlocutor is available all segues with "%s" won't be used.
 */
public class Segue {


    //region SegueType

    /**
     * Definitions of segue types here.
     *
     * Note: all segues are stored in this class for simplicity
     * and may be moved into a separate file later.
     */
    public enum SegueType {

        CONNECTING_PHRASE {
            @Override
            public RandomList<String> getPossibleSegues() {
                return PhraseCollection.CONNECTING_PHRASES;
            }
        },
        JOBS {
            @Override
            public RandomList<String> getPossibleSegues() {
                return PhraseCollection.SEGUE_JOBS;
            }
        },
        AVOID_ANSWER {
            @Override
            public RandomList<String> getPossibleSegues() {
                return PhraseCollection.SEGUE_AVOID_ANSWER;
            }
        },
        DISTRACT {
            @Override
            public RandomList<String> getPossibleSegues() {
                return PhraseCollection.SEGUE_DISTRACT;
            }
        },
        PICKUP {
            @Override
            public RandomList<String> getPossibleSegues() {
                return PhraseCollection.SEGUE_PICKUP;
            }
        },
        FLATTERY {
            @Override
            public RandomList<String> getPossibleSegues() {
                return PhraseCollection.SEGUE_FLATTERY;
            }
        },

        // segue types for testing purposes
        TEST_SEGUE {
            @Override
            public RandomList<String> getPossibleSegues() {
                return new RandomList<>("This is a test segue!");
            }
        },
        TEST_SEGUE_WITH_NAME {
            @Override
            public RandomList<String> getPossibleSegues() {
                return new RandomList<>("This is a test segue, interlocutor's name is %s.");
            }
        };



        /**
         * Returns a list of possible segues for this segue type as strings.
         * @return list of possible segues
         */
        public abstract RandomList<String> getPossibleSegues();
    }

    //endregion

    /** Default segue usage probability. */
    public static final double DEFAULT_PROBABILITY = 0.3;

    private final SegueType type;
    private final double probability;


    /**
     * Creates a segue based on a type and sets the default appearance probability.
     * @param type segue type
     */
    public Segue(SegueType type) {
        this(type, DEFAULT_PROBABILITY);
    }

    /**
     * Creates a segue based on a type and specified appearance probability.
     * Note that
     * @param type segue type
     * @param probability probability to use this segue in the conversation
     */
    public Segue(SegueType type, double probability) {
        if (type == null) {
            throw new RuntimeException("A segue must have a type: null is not allowed!");
        }

        this.type = type;

        if (probability > 1) probability = 1;
        if (probability < 0) probability = 0;
        this.probability = probability;
    }

    public SegueType getType() {
        return type;
    }

    public double getProbability() {
        return probability;
    }

}
