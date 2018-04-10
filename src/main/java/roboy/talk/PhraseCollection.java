package roboy.talk;

import roboy.util.IO;
import roboy.util.RandomList;

/**
 * A (temporary) central class to store short lists of phrases.
 * The lists are stored in separate files. This class loads all of them once at the
 * beginning, so all the lists can be used by any other class later.
 *
 * We might define a JSON format to replace the single files later.
 */
public class PhraseCollection {

    public static RandomList<String> SEGUE_AVOID_ANSWER
            = readFile("resources/phraseLists/segue-avoid-answer.txt");
    public static RandomList<String> SEGUE_CONNECTING_PHRASES
            = readFile("resources/phraseLists/segue-connecting-phrases.txt");
    public static RandomList<String> SEGUE_DISTRACT
            = readFile("resources/phraseLists/segue-distract.txt");
    public static RandomList<String> SEGUE_FLATTERY
            = readFile("resources/phraseLists/segue-flattery.txt");
    public static RandomList<String> SEGUE_JOBS
            = readFile("resources/phraseLists/segue-jobs.txt");
    public static RandomList<String> SEGUE_PICKUP
            = readFile("resources/phraseLists/segue-pickup.txt");

    private static RandomList<String> readFile(String path) {
        return new RandomList<>(IO.readLinesFromUtf8File(path));
    }

}
