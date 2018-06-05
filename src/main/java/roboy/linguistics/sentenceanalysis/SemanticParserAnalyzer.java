package roboy.linguistics.sentenceanalysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.linguistics.Linguistics;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public Interpretation analyze(Interpretation interpretation) {
        String response;
        SemanticAnalyzerInterface.Result result = semanticAnalyzer.analyze(
            (String) interpretation.getFeature("sentence"));
        interpretation.getFeatures().put(Linguistics.TOKENS, result.getTokens());
        interpretation.getFeatures().put(Linguistics.TRIPLE, extract_relations(result.getRelations()));
        interpretation.getFeatures().put(Linguistics.SENTIMENT, result.getSentiment());
        interpretation.getFeatures().put(Linguistics.POSTAGS, result.getPostags());
        interpretation.getFeatures().put(Linguistics.LEMMAS, result.getLemmaTokens());
        return interpretation;
        /*
        if (this.clientSocket != null && this.clientSocket.isConnected()) {
            try {
                String response;
                if (this.debug) {
                    logger.debug("SEMANTIC PARSER: " + interpretation.getSentence());
                }
                this.out.println(interpretation.getSentence());
                response = this.in.readLine();
                if (this.debug) {
                    logger.debug("> Full response:" + response);
                }
                if (response != null) {
                    // Convert JSON string back to Map.
                    Gson gson = new Gson();
                    Type type = new TypeToken<Map<String, Object>>() {
                    }.getType();
                    Type listStringsType = new TypeToken<List<String>>(){}.getType();

                    try {
                        Map<String, Object> full_response = gson.fromJson(response, type);
                        // Read formula and answer
                        if (full_response.containsKey("parse")) {
                            if (full_response.get("answer").toString().equals("(no answer)")) {
                                interpretation.setParsingOutcome(Linguistics.ParsingOutcome.FAILURE);
                            } else {
                                String answer = get_answers(full_response.get("answer").toString());
                                interpretation.setAnswer(answer);

                                interpretation.setParse(full_response.get("parse").toString());

                                List<Triple> triples = extract_triples(full_response.get("parse").toString());
                                interpretation.setSemTriples(triples);

                                interpretation.setParsingOutcome(Linguistics.ParsingOutcome.SUCCESS);
                            }
                        }
                        // Read followUp questions for underspecified terms
                        if (full_response.containsKey("followUpQ")) {
                            String specifyingQuestion = full_response.get("followUpQ").toString();
                            interpretation.setUnderspecifiedQuestion(specifyingQuestion);

                            String answer = get_answers(full_response.get("answer").toString());
                            interpretation.setUnderspecifiedAnswer(answer);
                            interpretation.setAnswer(answer);

                            interpretation.setParsingOutcome(Linguistics.ParsingOutcome.UNDERSPECIFIED);
                        }
                        // Read tokens
                        if (full_response.containsKey("tokens")) {
                            interpretation.setTokens(gson.fromJson(full_response.get("tokens").toString(), listStringsType));
                        }
                        // Read extracted non-semantic relations
                        if (full_response.containsKey("relations")) {
                            interpretation.setTriples(extract_relations((Map<String, Double>) full_response.get("relations")));
                        }
                        // Read extracted sentiment
                        if (full_response.containsKey("sentiment")) {
                            try {
                                interpretation.setSentiment(Linguistics.UtteranceSentiment.valueOf(full_response.get("sentiment").toString().toUpperCase()));
                            } catch (Exception e) {
                                interpretation.setSentiment(Linguistics.UtteranceSentiment.NEUTRAL);
                                logger.error("Sentiment is illegal: " + e.getMessage());
                            }
                        }
                        // Read POS-tags
                        if (full_response.containsKey("postags")) {
                            interpretation.setPosTags(gson.fromJson(full_response.get("postags").toString(), String[].class));
                        }
                        // Read lemmatized tokens
                        if (full_response.containsKey("lemma_tokens")) {
                            interpretation.setLemmas(gson.fromJson(full_response.get("lemma_tokens").toString(), String[].class));
                        }
                        // Read utterance type
                        if (full_response.containsKey("type")) {
                            interpretation.setUtteranceType(full_response.get("type").toString());
                        }
                    } catch (Exception e) {
                        logger.error("Exception while parsing semantic response: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                return interpretation;
            } catch (IOException e) {
                e.printStackTrace();
                return interpretation;
            }
        } else
            return interpretation;
        */
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
        input = input.replace(")", " )");
        input = input.replace("(", "( ");
        String[] tokens = input.split(" ");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].contains("triple") && i + 3 < tokens.length && !tokens[i].contains("triples")) {
                result.add(new Triple(tokens[i + 2], tokens[i + 1], tokens[i + 3]));
            } else if (tokens[i].contains("(") && i + 2 < tokens.length && tokens[i + 1].contains(":")) {
                if (tokens[i + 1].contains("!"))
                    result.add(new Triple(tokens[i + 1].replaceAll("!", ""), tokens[i + 2], null));
                else
                    result.add(new Triple(tokens[i + 1], null, tokens[i + 2]));
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