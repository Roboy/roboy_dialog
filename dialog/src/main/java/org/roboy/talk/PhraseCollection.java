package org.roboy.talk;

import org.roboy.util.FileLineReader;
import org.roboy.util.RandomList;
import roboy.util.FileLineReader;
import roboy.util.RandomList;

import java.util.ArrayList;
import java.util.List;

import static roboy.util.FileLineReader.readFile;


/**
 * A (temporary) central class to store short lists of phrases.
 * The lists are stored in separate files. This class loads all of them once at the
 * beginning, so all the lists can be used by any other class later.
 *
 * We might define a JSON format to replace the single files later.
 */
public class PhraseCollection {

    public static RandomList<String> CONNECTING_PHRASES
            = FileLineReader.readFile("resources/phraseLists/segue-connecting-phrases.txt");
    public static RandomList<String> QUESTION_ANSWERING_REENTERING
            = FileLineReader.readFile("resources/phraseLists/question-answering-reentering-phrases.txt");
    public static RandomList<String> QUESTION_ANSWERING_START
            = FileLineReader.readFile("resources/phraseLists/question-answering-starting-phrases.txt");
    public static RandomList<String> SEGUE_AVOID_ANSWER
            = FileLineReader.readFile("resources/phraseLists/segue-avoid-answer.txt");
    public static RandomList<String> SEGUE_DISTRACT
            = FileLineReader.readFile("resources/phraseLists/segue-distract.txt");
    public static RandomList<String> SEGUE_FLATTERY
            = FileLineReader.readFile("resources/phraseLists/segue-flattery.txt");
    public static RandomList<String> SEGUE_JOBS
            = FileLineReader.readFile("resources/phraseLists/segue-jobs.txt");
    public static RandomList<String> SEGUE_PICKUP
            = FileLineReader.readFile("resources/phraseLists/segue-pickup.txt");
    public static RandomList<String> OFFER_GAME_PHRASES
            = FileLineReader.readFile("resources/phraseLists/gamePhrases/offer-game-phrases.txt");
    public static RandomList<String> AKINATOR_INTRO_PHRASES
            = FileLineReader.readFile("resources/phraseLists/gamePhrases/akinator-intro-phrases.txt");
    public static RandomList<String> ROBOY_WINNER_PHRASES
            = FileLineReader.readFile("resources/phraseLists/gamePhrases/roboy-winner-phrases.txt");
    public static RandomList<String> ROBOY_LOSER_PHRASES
            = FileLineReader.readFile("resources/phraseLists/gamePhrases/roboy-loser-phrases.txt");
    public static RandomList<String> GAME_ASKING_PHRASES
            = FileLineReader.readFile("resources/phraseLists/gamePhrases/game-asking-phrases.txt");
    public static RandomList<String> OFFER_FILTER_PHRASES
            = FileLineReader.readFile("resources/phraseLists/gamePhrases/offer-snapchat-filter.txt");
    public static RandomList<String> AKINATOR_ERROR_PHRASES
            = FileLineReader.readFile("resources/phraseLists/gamePhrases/akinator-error-phrases.txt");
    public static RandomList<String> PROFANITY_CHECK_WORDS
            = FileLineReader.readFile("resources/phraseLists/profanity-check-list.txt");
    // Added for the ExpoPersonality
    public static RandomList<String> FACTS
            = FileLineReader.readFile("resources/phraseLists/expoPhrases/facts.txt");
    public static RandomList<String> INFO_ROBOY_INTENT_PHRASES
            = FileLineReader.readFile("resources/phraseLists/expoPhrases/info-roboy-intent.txt");
    public static RandomList<String> JOKES
            = FileLineReader.readFile("resources/phraseLists/expoPhrases/jokes.txt");
    public static RandomList<String> NEGATIVE_SENTIMENT_PHRASES
            = FileLineReader.readFile("resources/phraseLists/expoPhrases/negative-sentiment.txt");
    public static RandomList<String> OFFER_FACTS_PHRASES
            = FileLineReader.readFile("resources/phraseLists/expoPhrases/offer-facts.txt");
    public static RandomList<String> OFFER_FAMOUS_ENTITIES_PHRASES
            = FileLineReader.readFile("resources/phraseLists/expoPhrases/offer-famous-entities.txt");
    public static RandomList<String> OFFER_JOKES_PHRASES
            = FileLineReader.readFile("resources/phraseLists/expoPhrases/offer-jokes.txt");
    public static RandomList<String> OFFER_MATH_PHRASES
            = FileLineReader.readFile("resources/phraseLists/expoPhrases/offer-math.txt");
    public static RandomList<String> PARSER_ERROR
            = FileLineReader.readFile("resources/phraseLists/expoPhrases/parser-error.txt");

}
