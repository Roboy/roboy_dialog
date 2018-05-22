package roboy.linguistics.sentenceanalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.emotions.RoboyEmotion;
import roboy.linguistics.DetectedEntity;
import roboy.linguistics.Linguistics.*;
import roboy.linguistics.Triple;

/**
 * An interpretation of all inputs to Roboy consists of the sentence type and an
 * arbitrary map of features. Feature names are listed and documented in the
 * class roboy.linguistics.Linguistics.
 *
 * The interpretation class is also used to pass the output information from the
 * states to the verbalizer class.
 */
public class Interpretation {

    private final Logger LOGGER = LogManager.getLogger();

	//private Map<String,Object> features;
	private SentenceType sentenceType;
    private String sentence = null;
    private List<Triple> triples = null;
    private List<Triple> semTriples = null;
    private List<String> tokens = null;
    private String[] posTags = null;
    private String[] lemmas = null;
    private List<DetectedEntity> keywords = null;
    private String association = null;
    private Map<SemanticRole, String> pas = null;
    private String name = null;
    private String celebrity = null;
    private boolean isRoboy = false;
    private String objAnswer = null;
    private String predAnswer = null;
    private RoboyEmotion emotion = null;
    private String intent = null;
    private String intentDistance = null;

    private String parse = null;
    private String parseAnswer = null;
    private String underspecifiedTermQuestion = null;
    private String underspecifiedQuestion = null;
    private String underspecifiedAnswer = null;
    private UtteranceSentiment sentiment = null;
    private String utteranceType = null;
    private ParsingOutcome parsingOutcome = null;


    // new type safe fields to use with semantic parser, refactor to private fields + getter & setter later
	// getting Objects without any type information from the hash map is not a good practice, this is not JavaScript
	private String answer;
	public List<Triple> semParserTriples;

	public Interpretation(String sentence){
		//features = new HashMap<>();
		//features.put(SENTENCE, sentence);
		this.sentenceType = SentenceType.STATEMENT;
		this.sentence = sentence;
	}

	public Interpretation(String sentence, Map<String,Object> features){
		//this.features = features;
		//this.features.put(SENTENCE, sentence);
		this.sentenceType = SentenceType.STATEMENT;
		this.sentence = sentence;
	}

	public Interpretation(SentenceType sentenceType){
		this.sentenceType = sentenceType;
		//features = new HashMap<>();
	}

	public Interpretation(SentenceType sentenceType, String sentence, Triple triple){
		this.sentenceType = sentenceType;
		this.sentence = sentence;
		this.triples = new ArrayList<>();
		this.triples.add(triple);
	}

	public Interpretation(Interpretation interpretation) {
	    LOGGER.info("Merge/copy method is under construction! See you soon!");
    }

	//public Map<String, Object> getFeatures() {
	//	return features;
	//}

	//public Object getFeature(String featureName){
	//	return features.get(featureName);
	//}

	//public void setFeatures(Map<String, Object> features) {
	//	this.features = features;
	//}

	public SentenceType getSentenceType() {
		return sentenceType;
	}

	public void setSentenceType(SentenceType sentenceType) {
		this.sentenceType = sentenceType;
	}

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public List<Triple> getTriples() {
        return triples;
    }

    public void setTriples(List<Triple> triple) {
        this.triples = triple;
    }

    public List<Triple> getSemTriples() {
        return semTriples;
    }

    public void setSemTriples(List<Triple> semTriples) {
        this.semTriples = semTriples;
        this.semTriples.removeIf(Objects::isNull);
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
        //this.tokens.removeIf(Objects::isNull);
    }

    public String[] getPosTags() {
        return posTags;
    }

    public void setPosTags(String[] posTags) {
        this.posTags = posTags;
    }

    public String[] getLemmas() {
        return lemmas;
    }

    public void setLemmas(String[] lemmas) {
        this.lemmas = lemmas;
    }

    public List<DetectedEntity> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<DetectedEntity> keywords) {
        this.keywords = keywords;
    }

    public void addKeyword(DetectedEntity keyword) {
	    if (keywords == null) {
            keywords = new ArrayList<>();
        }
        keywords.add(keyword);
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public Map<SemanticRole, String> getPas() {
        return pas;
    }

    public void setPas(Map<SemanticRole, String> pas) {
        this.pas = pas;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCelebrity() {
        return celebrity;
    }

    public void setCelebrity(String celebrity) {
        this.celebrity = celebrity;
    }

    public boolean isRoboy() {
        return isRoboy;
    }

    public void setRoboy(boolean roboy) {
        isRoboy = roboy;
    }

    public String getObjAnswer() {
        return objAnswer;
    }

    public void setObjAnswer(String objAnswer) {
        this.objAnswer = objAnswer;
    }

    public String getPredAnswer() {
        return predAnswer;
    }

    public void setPredAnswer(String predAnswer) {
        this.predAnswer = predAnswer;
    }

    public RoboyEmotion getEmotion() {
        return emotion;
    }

    public void setEmotion(RoboyEmotion emotion) {
        this.emotion = emotion;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getIntentDistance() {
        return intentDistance;
    }

    public void setIntentDistance(String intentDistance) {
        this.intentDistance = intentDistance;
    }

    public String getParse() {
        return parse;
    }

    public void setParse(String parse) {
        this.parse = parse;
    }

    public String getParseAnswer() {
        return parseAnswer;
    }

    public void setParseAnswer(String parseAnswer) {
        this.parseAnswer = parseAnswer;
    }

    public String getUnderspecifiedTermQuestion() {
        return underspecifiedTermQuestion;
    }

    public void setUnderspecifiedTermQuestion(String underspecifiedTermQuestion) {
        this.underspecifiedTermQuestion = underspecifiedTermQuestion;
    }

    public String getUnderspecifiedQuestion() {
        return underspecifiedQuestion;
    }

    public void setUnderspecifiedQuestion(String underspecifiedQuestion) {
        this.underspecifiedQuestion = underspecifiedQuestion;
    }

    public String getUnderspecifiedAnswer() {
        return underspecifiedAnswer;
    }

    public void setUnderspecifiedAnswer(String underspecifiedAnswer) {
        this.underspecifiedAnswer = underspecifiedAnswer;
    }

    public UtteranceSentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(UtteranceSentiment sentiment) {
        this.sentiment = sentiment;
    }

    public String getUtteranceType() {
        return utteranceType;
    }

    public void setUtteranceType(String utteranceType) {
        this.utteranceType = utteranceType;
    }

    public ParsingOutcome getParsingOutcome() {
        return parsingOutcome;
    }

    public void setParsingOutcome(ParsingOutcome parsingOutcome) {
        this.parsingOutcome = parsingOutcome;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void addTriple(Triple triple) {
	    if (triples == null) {
            triples = new ArrayList<>();
        }
        triples.add(triple);
    }

    @Override
    public String toString() {
        return "Interpretation{" +
                //"features=" + features +
                ", sentenceType=" + sentenceType +
                ", sentence='" + sentence + '\'' +
                ", triple='" + triples + '\'' +
                ", semTriples='" + semTriples + '\'' +
                ", tokens='" + tokens + '\'' +
                ", posTags='" + posTags + '\'' +
                ", lemmas='" + lemmas + '\'' +
                ", keywords='" + keywords + '\'' +
                ", association='" + association + '\'' +
                ", pas='" + pas + '\'' +
                ", name='" + name + '\'' +
                ", celebrity='" + celebrity + '\'' +
                ", isRoboy=" + isRoboy +
                ", objAnswer='" + objAnswer + '\'' +
                ", predAnswer='" + predAnswer + '\'' +
                ", emotion='" + emotion + '\'' +
                ", intent='" + intent + '\'' +
                ", intentDistance='" + intentDistance + '\'' +
                ", parse='" + parse + '\'' +
                ", parseAnswer='" + parseAnswer + '\'' +
                ", underspecifiedTermQuestion='" + underspecifiedTermQuestion + '\'' +
                ", underspecifiedQuestion='" + underspecifiedQuestion + '\'' +
                ", underspecifiedAnswer='" + underspecifiedAnswer + '\'' +
                ", sentiment=" + sentiment +
                ", utteranceType=" + utteranceType +
                ", parserResult='" + parsingOutcome + '\'' +
                ", answer='" + answer + '\'' +
                ", semParserTriples=" + semParserTriples +
                ", parsingOutcome=" + parsingOutcome +
                '}';
    }
}
