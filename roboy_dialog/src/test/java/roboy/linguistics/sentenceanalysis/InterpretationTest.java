package roboy.linguistics.sentenceanalysis;

import org.junit.Before;
import org.junit.Test;
import roboy.emotions.RoboyEmotion;
import roboy.linguistics.DetectedEntity;
import roboy.linguistics.Entity;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class InterpretationTest {
    private Interpretation analyzedInterpretation;
    private Interpretation defaultInterpretation;
    private Linguistics.SentenceType sentenceType;
    private String sentence;
    private Triple triple;
    private List<Triple> triples;
    private List<Triple> semTriples;
    private List<String> tokens;
    private String[] posTags;
    private String[] lemmas;
    private Entity entity;
    private DetectedEntity detectedEntity;
    private List<DetectedEntity> keywords;
    private String association;
    private Map<Linguistics.SemanticRole, String> pas;
    private String name;
    private String celebrity;
    private boolean isRoboy ;
    private String objAnswer;
    private String predAnswer;
    private RoboyEmotion emotion;
    private String intent;
    private String intentDistance;
    private String parse;
    private String parseAnswer;
    private String underspecifiedTermQuestion;
    private String underspecifiedQuestion;
    private String underspecifiedAnswer;
    private Linguistics.UtteranceSentiment sentiment;
    private String utteranceType;
    private Linguistics.ParsingOutcome parsingOutcome;
    private String answer;

    @Before
    public void setUp() throws Exception {
        analyzedInterpretation = new Interpretation();
        defaultInterpretation = new Interpretation();
        sentenceType = Linguistics.SentenceType.STATEMENT;
        sentence = "test sentence";
        triple = new Triple("subject", "predicate", "object");
        triples = new ArrayList<>();
        triples.add(triple);
        semTriples = new ArrayList<>();
        semTriples.add(triple);
        tokens = new ArrayList<>();
        tokens.add("test");
        tokens.add("sentence");
        posTags = new String[]{"NN", "NN"};;
        lemmas = new String[]{"test", "sentence"};
        entity = new Entity("test");
        detectedEntity = new DetectedEntity(0, entity);
        keywords = new ArrayList<>();
        keywords.add(detectedEntity);
        association = "quiz";
        pas = new HashMap<>();
        name = "name";
        celebrity = "test celebrity";
        isRoboy = true;
        objAnswer = "object";
        predAnswer = "predicate";
        emotion = RoboyEmotion.NEUTRAL;
        intent = "neo4j intent";
        intentDistance = "0";
        parse = "parse";
        parseAnswer = "parse answer";
        underspecifiedTermQuestion = "underspecified term question";
        underspecifiedQuestion = "underspecified question";
        underspecifiedAnswer = "underspecified answer";
        sentiment = Linguistics.UtteranceSentiment.NEUTRAL;
        utteranceType = "utterance";
        parsingOutcome = Linguistics.ParsingOutcome.SUCCESS;

        defaultInterpretation = new Interpretation();
        defaultInterpretation.setSentence(sentence);
        defaultInterpretation.setSentenceType(sentenceType);
        defaultInterpretation.setRoboy(isRoboy);
        defaultInterpretation.setCelebrity(celebrity);
        defaultInterpretation.setPredAnswer(predAnswer);
        defaultInterpretation.setObjAnswer(objAnswer);
        defaultInterpretation.setParsingOutcome(parsingOutcome);
        defaultInterpretation.setTokens(tokens);
        defaultInterpretation.setPosTags(posTags);
        defaultInterpretation.setUtteranceType(utteranceType);
        defaultInterpretation.setIntentDistance(intentDistance);
        defaultInterpretation.setIntent(intent);
        defaultInterpretation.setTriples(triples);
        defaultInterpretation.setParse(parse);
        defaultInterpretation.setAnswer(answer);
        defaultInterpretation.setSentiment(sentiment);
        defaultInterpretation.setLemmas(lemmas);
        defaultInterpretation.setEmotion(RoboyEmotion.NEUTRAL);
        defaultInterpretation.setPas(pas);
        defaultInterpretation.setSemTriples(semTriples);
        defaultInterpretation.setAssociation(association);
        defaultInterpretation.setKeywords(keywords);
        defaultInterpretation.setName(name);
        defaultInterpretation.setParseAnswer(parseAnswer);
        defaultInterpretation.setUnderspecifiedQuestion(underspecifiedQuestion);
        defaultInterpretation.setUnderspecifiedTermQuestion(underspecifiedTermQuestion);
        defaultInterpretation.setUnderspecifiedAnswer(underspecifiedAnswer);

        // TODO: How do we encapsulate the analyzers
        // AnalyzerInterface analyzerInterface = Analyzer(semanticParser, emotionAnalyzer, openNLPParser)
        // TODO: Need to call them on the sentence
        // analyzedInterpretation = AnanlyzerInterface.analyze(sentence)
        analyzedInterpretation.copy(defaultInterpretation);
    }

    @Test
    public void getSentenceType() {
        assertEquals(sentenceType, defaultInterpretation.getSentenceType());
        assertEquals(defaultInterpretation.getSentenceType(), analyzedInterpretation.getSentenceType());
    }

    @Test
    public void getSentence() {
        assertEquals(sentence, defaultInterpretation.getSentence());
        assertEquals(defaultInterpretation.getSentence(), analyzedInterpretation.getSentence());
    }

    @Test
    public void getTriples() {
        assertEquals(triples, defaultInterpretation.getTriples());
        assertEquals(triple, defaultInterpretation.getTriples().get(0));
        assertEquals(defaultInterpretation.getTriples(), analyzedInterpretation.getTriples());
    }

    @Test
    public void getSemTriples() {
        assertEquals(semTriples, defaultInterpretation.getSemTriples());
        assertEquals(defaultInterpretation.getSemTriples(), analyzedInterpretation.getSemTriples());
    }

    @Test
    public void getTokens() {
        assertEquals(tokens, defaultInterpretation.getTokens());
        assertEquals(defaultInterpretation.getTokens(), analyzedInterpretation.getTokens());
    }

    @Test
    public void getPosTags() {
        assertArrayEquals(posTags, defaultInterpretation.getPosTags());
        assertArrayEquals(defaultInterpretation.getPosTags(), analyzedInterpretation.getPosTags());
    }

    @Test
    public void getLemmas() {
        assertArrayEquals(lemmas, defaultInterpretation.getLemmas());
        assertArrayEquals(defaultInterpretation.getLemmas(), analyzedInterpretation.getLemmas());
    }

    @Test
    public void getKeywords() {
        assertEquals(keywords, defaultInterpretation.getKeywords());
        assertEquals(defaultInterpretation.getKeywords(), analyzedInterpretation.getKeywords());
    }

    @Test
    public void getAssociation() {
        assertEquals(association, defaultInterpretation.getAssociation());
        assertEquals(defaultInterpretation.getAssociation(), analyzedInterpretation.getAssociation());
    }

    @Test
    public void getPas() {
        assertEquals(pas, defaultInterpretation.getPas());
        assertEquals(defaultInterpretation.getPas(), analyzedInterpretation.getPas());
    }

    @Test
    public void getName() {
        assertEquals(name, defaultInterpretation.getName());
        assertEquals(defaultInterpretation.getName(), analyzedInterpretation.getName());
    }

    @Test
    public void getCelebrity() {
        assertEquals(celebrity, defaultInterpretation.getCelebrity());
        assertEquals(defaultInterpretation.getCelebrity(), analyzedInterpretation.getCelebrity());
    }

    @Test
    public void isRoboy() {
        assertEquals(isRoboy, defaultInterpretation.isRoboy());
        assertEquals(defaultInterpretation.isRoboy(), analyzedInterpretation.isRoboy());
    }

    @Test
    public void getObjAnswer() {
        assertEquals(objAnswer, defaultInterpretation.getObjAnswer());
        assertEquals(defaultInterpretation.getObjAnswer(), analyzedInterpretation.getObjAnswer());
    }

    @Test
    public void getPredAnswer() {
        assertEquals(predAnswer, defaultInterpretation.getPredAnswer());
        assertEquals(defaultInterpretation.getPredAnswer(), analyzedInterpretation.getPredAnswer());
    }

    @Test
    public void getEmotion() {
        assertEquals(emotion, defaultInterpretation.getEmotion());
        assertEquals(defaultInterpretation.getEmotion(), analyzedInterpretation.getEmotion());
    }

    @Test
    public void getIntent() {
        assertEquals(intent, defaultInterpretation.getIntent());
        assertEquals(defaultInterpretation.getIntent(), analyzedInterpretation.getIntent());
    }

    @Test
    public void getIntentDistance() {
        assertEquals(intentDistance, defaultInterpretation.getIntentDistance());
        assertEquals(defaultInterpretation.getIntentDistance(), analyzedInterpretation.getIntentDistance());
    }

    @Test
    public void getParse() {
        assertEquals(parse, defaultInterpretation.getParse());
        assertEquals(defaultInterpretation.getParse(), analyzedInterpretation.getParse());
    }

    @Test
    public void getParseAnswer() {
        assertEquals(parseAnswer, defaultInterpretation.getParseAnswer());
        assertEquals(defaultInterpretation.getParseAnswer(), analyzedInterpretation.getParseAnswer());
    }

    @Test
    public void getUnderspecifiedTermQuestion() {
        assertEquals(underspecifiedTermQuestion, defaultInterpretation.getUnderspecifiedTermQuestion());
        assertEquals(defaultInterpretation.getUnderspecifiedTermQuestion(), analyzedInterpretation.getUnderspecifiedTermQuestion());
    }

    @Test
    public void getUnderspecifiedQuestion() {
        assertEquals(underspecifiedQuestion, defaultInterpretation.getUnderspecifiedQuestion());
        assertEquals(defaultInterpretation.getUnderspecifiedQuestion(), analyzedInterpretation.getUnderspecifiedQuestion());
    }

    @Test
    public void getUnderspecifiedAnswer() {
        assertEquals(underspecifiedAnswer, defaultInterpretation.getUnderspecifiedAnswer());
        assertEquals(defaultInterpretation.getUnderspecifiedAnswer(), analyzedInterpretation.getUnderspecifiedAnswer());
    }

    @Test
    public void getSentiment() {
        assertEquals(sentiment, defaultInterpretation.getSentiment());
        assertEquals(defaultInterpretation.getSentiment(), analyzedInterpretation.getSentiment());
    }

    @Test
    public void getUtteranceType() {
        assertEquals(utteranceType, defaultInterpretation.getUtteranceType());
        assertEquals(defaultInterpretation.getUtteranceType(), analyzedInterpretation.getUtteranceType());
    }

    @Test
    public void getParsingOutcome() {
        assertEquals(parsingOutcome, defaultInterpretation.getParsingOutcome());
        assertEquals(defaultInterpretation.getParsingOutcome(), analyzedInterpretation.getParsingOutcome());
    }

    @Test
    public void getAnswer() {
        assertEquals(answer, defaultInterpretation.getAnswer());
        assertEquals(defaultInterpretation.getAnswer(), analyzedInterpretation.getAnswer());
    }

    @Test
    public void copy() {
        Interpretation interpretation = new Interpretation();
        interpretation.copy(defaultInterpretation);
        assertEquals(defaultInterpretation, interpretation);
    }
}