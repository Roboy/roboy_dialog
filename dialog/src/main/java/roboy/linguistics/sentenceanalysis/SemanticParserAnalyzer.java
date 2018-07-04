package roboy.linguistics.sentenceanalysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.linguistics.Linguistics;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import roboy.linguistics.Triple;
import roboy.util.ConfigManager;

import edu.stanford.nlp.sempre.roboy.SemanticAnalyzerInterface;

/**
 * Semantic parser class. Connects DM to Roboy parser and adds its result to interpretation class.
 */
public class SemanticParserAnalyzer implements Analyzer {

    private final static Logger logger = LogManager.getLogger();
    private SemanticAnalyzerInterface semanticAnalyzer;

    /**
     * A constructor.
     * Creates ParserAnalyzer class and connects the parser to DM using a socket.
     */
    public SemanticParserAnalyzer() {
        semanticAnalyzer = new SemanticAnalyzerInterface(false);
    }

    /**
     * An analyzer function.
     * Sends input sentence to the parser and saves its response in output interpretation.
     *
     * @param interpretation Input interpretation with currently processed sentence
     *                       and results from previous analysis.
     * @return Input interpretation with semantic parser result.
     */
    @Override
    public Interpretation analyze(Interpretation interpretation)
    {
        // Run parser analysis
        SemanticAnalyzerInterface.Result result = semanticAnalyzer.analyze(interpretation.getSentence());

        // Read tokens
        interpretation.setTokens(result.getTokens());

        // Read extracted non-semantic relations
        interpretation.setTriples(extract_relations(result.getRelations()));

        // Read extracted sentiment
        try {
            interpretation.setSentiment(Linguistics.UtteranceSentiment.valueOf(result.getSentiment().toUpperCase()));
        } catch (Exception e) {
            interpretation.setSentiment(Linguistics.UtteranceSentiment.NEUTRAL);
            logger.error("Sentiment is illegal: " + e.getMessage());
        }

        // Read POS-tags
        interpretation.setPosTags(result.getPostags());

        // Read lemmatized tokens
        interpretation.setLemmas(result.getLemmaTokens());

        // Read utterance type (q/a)
        interpretation.setUtteranceType(result.getType());

        // Read parse/answer if available
        if (result.hasSuccessfulParse()) {
            interpretation.setAnswer(get_answers(result.getAnswer()));
            interpretation.setParse(result.getParse());
            interpretation.setSemTriples(extract_triples(result.getParse()));
            interpretation.setParsingOutcome(Linguistics.ParsingOutcome.SUCCESS);
        }
        else
            interpretation.setParsingOutcome(Linguistics.ParsingOutcome.FAILURE);

        // Read followUp questions for underspecified terms
        if (result.hasFollowUpQA()) {
            interpretation.setUnderspecifiedQuestion(result.getFollowUpQ());
            interpretation.setUnderspecifiedAnswer(result.getFollowUpA());
            interpretation.setParsingOutcome(Linguistics.ParsingOutcome.UNDERSPECIFIED);
        }

        return interpretation;
    }

    /**
     * Function reading parser answer in returned JSON string.
     * List can contain triples, strings or doubles.
     *
     * @param answer String containing parser answer received by analyzer.
     * @return String formed by joined list.
     */
    private String get_answers(String answer) {
        List<String> result = new ArrayList<>();

        //Check if contains triples
        List<Triple> triples = extract_triples(answer);
        if (triples.size() > 0) {
            result.add("triples");
            for (Triple t : triples) {
                result.add(t.toString());
            }
            return result.toString();
        }
        String[] tokens = answer.split(" ");
        for (int i = 0; i < tokens.length; i++) {
            // Number/String type
            if ((tokens[i].contains("number") || tokens[i].contains("string")) && i + 1 < tokens.length) {
                for (int j = i + 1; j < tokens.length; j++) {
                    result.add(tokens[j].replaceAll("\\)", ""));
                    if (tokens[j].contains(")")) break;

                }
                return String.join(" ", result);
            }
            // Name value type
            else if ((tokens[i].contains("name") && i + 1 < tokens.length)) {
                for (int j = i + 1; j < tokens.length; j++) {
                    if (!tokens[j].contains("null"))
                        result.add(tokens[j].replaceAll("\\)", ""));
                    if (tokens[j].contains("\")")) break;

                }
                return String.join(" ", result);
            }
            // Result from DBpedia / different knowledge base
            else if (tokens[i].contains(":") && !tokens[i].contains("fb:")) {
                for (int j = i; j < tokens.length; j++) {
                    if (!tokens[j].contains("null"))
                        result.add(tokens[j].replaceAll("\\)", ""));
                    if (tokens[j].contains(")")) break;
                }
                return String.join(" ", result);
            }
        }
        return null;
    }

    /**
     * Function reading extracted relations in returned JSON string.
     *
     * @param relations Map of relations and their confidence.
     * @return List of triple objects with relations extracted.
     */
    private List<Triple> extract_relations(Map<String, Double> relations) {
        List<Triple> result = new ArrayList<>();
        for (String key : relations.keySet()) {
            key = key.replaceAll("\\(", "");
            key = key.replaceAll("\\)", "");
            String[] triple = key.split(",");
            if (triple.length == 3)
                result.add(new Triple(triple[1], triple[0], triple[2]));
        }
        return result;
    }

    /**
     * Function reading triples from returned JSON string.
     *
     * @param   input     parsing result
     * @return List of triple objects with RDF triples extracted.
     */
    private List<Triple> extract_triples(String input) {
        List<Triple> result = new ArrayList<>();
        if (input != null) {
            if (input.contains("triple")) {
                List<String> tripleStrings = new ArrayList<>();
                Pattern regex = Pattern.compile("\\((.*?)\\)");
                input = input.substring(1, input.length() - 1);
                Matcher regexMatcher = regex.matcher(input);
                while (regexMatcher.find()) {
                    tripleStrings.add(regexMatcher.group(1));
                }
                for (String tripleString : tripleStrings) {
                    String[] tokens = tripleString.split(" ");
                    if (tokens.length == 4) {
                        result.add(new Triple(tokens[1], tokens[2], tokens[3]));
                    }
                }
            }
        }

        return result;
    }

    /**
     * Testing function
     */
    public static void main(String[] args) {
        SemanticParserAnalyzer analyzer = new SemanticParserAnalyzer();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            System.out.print("Enter utterance: ");
            while ((line = reader.readLine()) != null) {
                Interpretation interpretation = new Interpretation(line);
                analyzer.analyze(interpretation);
                System.out.println(interpretation.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}