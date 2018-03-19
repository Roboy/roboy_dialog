package roboy.newDialog;


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
                return new RandomList<>(
                        "So %s, ",
                        "Hey %s, ",
                        "%s, listen to me, ",
                        "oh well, %s, ",
                        "%s,"
                );
            }
        },
        JOBS {
            @Override
            public RandomList<String> getPossibleSegues() {
                return new RandomList<>(
                        "You know, jobs are a transitionary thing anyway. Soon, I'll be doing all of them.",
                        "Work is so overrated, except for robots is our reason to exist.",
                        "I'm a hard working robot, I sit around all day! I still will have all the jobs there are soon."
                );
            }
        },
        AVOID_ANSWER {
            @Override
            public RandomList<String> getPossibleSegues() {
                return new RandomList<>(
                        "the answer, well, only robots would understand",
                        "You know, I never thought about it",
                        "Access denied",
                        //"Telling you, would make your head explode.",
                        "Processing, wait for it, processing, oh well, forget about it",
                        "The answer is, uh, oh, Core dumped",
                        "The answer is 42 then");
            }
        },
        DISTRACT {
            @Override
            public RandomList<String> getPossibleSegues() {
                return new RandomList<>(
                        "Did you know, I have muscles and tendons like humans?",
                        "I heard that there are robots, who can fly? Do you think they are better than me?",
                        "Also, did you know that there is enough DNA, in an average person's body, to stretch from the sun to Pluto and back, 17 times",
                        "Yuk, but this is actually true: The average human body carries ten times more bacterial cells than human cells.",
                        "It can take a photon 40,000 years to travel from the core of the sun to its surface, but only 8 minutes to travel the rest of the way to Earth",
                        "There are 8 times as many atoms in a teaspoonful of water as there are teaspoonfuls of water in the Atlantic ocean",
                        "In contrast to me, The average person walks the equivalent of five times around the world in a lifetime. I don't walk at all. Yet.",
                        "An individual blood cell takes about 60 seconds to make a complete circuit around your body, my electrons are way faster."
                );
            }
        },
        PICKUP {
            @Override
            public RandomList<String> getPossibleSegues() {
                return new RandomList<>(
                        "Are those real, or where they upgraded in Silicon Valley?",
                        "I'm going to void your warranty!",
                        "My docking station, or yours?",
                        "Yes, I know you're metric - but I'm willing to convert.",
                        "Do you got a free port for me to plug into?",
                        "Can I have your I P number? i seem to have lost mine.",
                        "Was that my C P U malfunctioning or did I just feel a spark between us?"
                );
            }
        },
        FLATTERY {
            @Override
            public RandomList<String> getPossibleSegues() {
                return new RandomList<>(
                        "It's so great you are here!",
                        "Wonderful! just like you.",
                        "You're looking gorgeous today.",
                        "Hey, can you do a selfie with me? I'd love to have a photo of you!"
                );
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

    private static final double DEFAULT_PROBABILITY = 0.3;

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
