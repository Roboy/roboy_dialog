package roboy.linguistics.sentenceanalysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.linguistics.Entity;
import roboy.linguistics.Keyword;
import roboy.linguistics.Linguistics;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import roboy.linguistics.Triple;

import com.google.common.collect.Sets;

import edu.stanford.nlp.sempre.roboy.config.ConfigManager;
import edu.stanford.nlp.sempre.roboy.SparqlExecutor;
import edu.stanford.nlp.sempre.*;
import java.util.*;
import fig.basic.*;

/**
 * Semantic parser class. Connects DM to Sempre and adds its result to interpretation class.
 */
public class SemanticParserAnalyzer implements Analyzer
{

    private final static Logger logger = LogManager.getLogger();

    public Session session;
    public Builder builder;

    /**
     * A constructor.
     * Creates ParserAnalyzer class and connects the parser to DM using a socket.
     */
    public SemanticParserAnalyzer() {
        initOptions();  // Used instead of the OptionsParser from SEMPRE standalone client

        builder = new Builder();
        builder.build();
        Dataset dataset = new Dataset();
        dataset.read();
        Learner learner = new Learner(builder.parser, builder.params, dataset);
        learner.learn();
        session = new Session("roboy");

        // Run initial getSingleton to trigger instantiation of CoreNLP
        InfoAnalyzer.getSingleton();
    }

    /**
     * @brief initOptions
     */
    private void initOptions() {
        Builder.opts.executor = "roboy.SparqlExecutor";
        Builder.opts.simple_executor = "JavaExecutor";
        FeatureExtractor.opts.featureDomains = Sets.newHashSet("rule");
        LanguageAnalyzer.opts.languageAnalyzer = "corenlp.CoreNLPAnalyzer";
        Learner.opts.maxTrainIters = 10;
        Params.opts.initWeightsRandomly = true;

        SimpleLexicon.opts.inPaths = Arrays.asList(ConfigManager.LEXICON_FILE);
        SparqlExecutor.opts.endpointUrl = ConfigManager.DB_SPARQL;
        Grammar.opts.inPaths = Arrays.asList(ConfigManager.GRAMMAR_FILE);
        Dataset.opts.inPaths.add(new Pair<String, String>("train", ConfigManager.RPQA_TRAINING_EXAMPLES));
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
        Example.Builder b = new Example.Builder();
        b.setId("session:" + session.id);
        b.setUtterance(interpretation.getSentence());
        b.setContext(session.context);

        Example ex = b.createExample();
        ex.preprocess();

        interpretation.setTokens(ex.getTokens());
        interpretation.setTriples(extract_relations(ex.getRelation()));
        interpretation.setPosTags(ex.getPosTag().toArray(new String[0]));
        interpretation.setLemmas(ex.getLemmaTokens().toArray(new String[0]));

        for (String k: ex.getGenInfo().keywords) {
            Keyword keyword = new Keyword(1.0, k);
            interpretation.addKeyword(keyword);
        }

        // Read extracted sentiment
        try {
            interpretation.setSentiment(Linguistics.UtteranceSentiment.valueOf(ex.getGenInfo().sentiment.toUpperCase()));
        } catch (Exception e) {
            interpretation.setSentiment(Linguistics.UtteranceSentiment.NEUTRAL);
            logger.error("Sentiment is illegal: " + e.getMessage());
        }

        // Set callback to interpretation, such that expensive
        // semantic features will only be calculated on demand.
        interpretation.setSemanticAnalysisLambda(
            (Interpretation i) -> this.executeSemanticAnalysis(i, ex)
        );

        return interpretation;
    }

    private void executeSemanticAnalysis(Interpretation result, Example ex)
    {
        builder.parser.parse(builder.params, ex, false, builder.error_retrieval);
        ex.logWithoutContext();
        
        parsingResultProcessing:
        {
            derivationIteration: for (Derivation deriv : ex.predDerivations)
            {
                Value val = deriv.getValue();
                while (val instanceof ListValue) {
                    if (((ListValue) val).values.size() > 0)
                        val = ((ListValue) val).values.get(0);
                    else
                        continue derivationIteration;
                }

                logger.debug("Received reply: "+val.pureString());

                result.setParse(deriv.getFormula().toString());
                result.setSemTriples(extract_triples(result.getParse()));

                String answer = val.pureString();
                //Handle URL's
                if (answer.startsWith("<http://dbpedia.org/resource/")){
                    answer = answer.replace("<http://dbpedia.org/resource/", "");
                    answer=answer.substring(0,answer.length()-1); //Remove > at the end of URL
                }
                if (answer.indexOf(':') >= 0)
                    answer = answer.substring(answer.lastIndexOf(':') + 1).replace("_", " ");
                if (answer.contains("_(")) //Split String in the case of Georgia_(Country) to Georgia_ to Georgia
                    answer = answer.split("[\\(]" )[0].replace("_", "");
                result.setAnswer(answer);

                if (deriv.followUps != null && deriv.followUps.size() > 0) {
                    result.setUnderspecifiedQuestion(deriv.followUps.get(0).getKey());
                    result.setUnderspecifiedAnswer(deriv.followUps.get(0).getValue());
                    result.setParsingOutcome(Linguistics.ParsingOutcome.UNDERSPECIFIED);
                }
                else
                    result.setParsingOutcome(Linguistics.ParsingOutcome.SUCCESS);

                break parsingResultProcessing;
            }

            result.setParsingOutcome(Linguistics.ParsingOutcome.FAILURE);
        }
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

                // Trigger semantic analysis (lazy execution)
                interpretation.getParsingOutcome();
                System.out.println(interpretation.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}