package roboy.talk;

import roboy.util.IO;
import roboy.util.RandomList;

import java.util.ArrayList;
import java.util.List;

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
    public static RandomList<String> QUESTION_ANSWERING_REENTERING
            = readFile("resources/phraseLists/question-answering-reentering-phrases.txt");
    public static RandomList<String> QUESTION_ANSWERING_START
            = readFile("resources/phraseLists/question-answering-starting-phrases.txt");
    public static RandomList<String> SEGUE_BORED
            = readFile("resources/phraseLists/bored.txt");
    public static RandomList<String> SNAPCHAT_FILTERS
            = readFile("resources/snapchatFilters/snapchat-filters.txt");

    private static RandomList<String> readFile(String path) {
        RandomList<String> result = new RandomList<>();
        List<String> phrasesFromFile = IO.readLinesFromUtf8File(path);
        if (phrasesFromFile == null) {
            return result;
        }
        // ignore empty strings
        for (String s : phrasesFromFile) {
            if (s.trim().length() > 0) {
                // add spaces to prevent cases when someone forgets to add a space in between
                result.add(" " + s + " ");
            }
        }
        return result;
    }

}
