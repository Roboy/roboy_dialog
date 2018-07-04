package roboy.linguistics.sentenceanalysis;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.emotions.RoboyEmotion;
import roboy.linguistics.DetectedEntity;
import roboy.linguistics.Linguistics.*;
import roboy.linguistics.Triple;

import javax.annotation.Nullable;

/**
 * An interpretation of all inputs to Roboy consists of the sentence type and an
 * arbitrary map of features. Feature names are listed and documented in the
 * class roboy.linguistics.Linguistics.
 *
 * The interpretation class is also used to pass the output information from the
 * states to the verbalizer class.
 */
public class Interpretation implements Cloneable {

    private final Logger LOGGER = LogManager.getLogger();
	private SentenceType sentenceType = null;
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
    private boolean profanity = false;
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

	public Interpretation() {
	    LOGGER.debug("Empty Interpretation initialized");
    }

	public Interpretation(String sentence){
		this.sentenceType = SentenceType.STATEMENT;
		this.sentence = sentence;
        LOGGER.debug("Interpretation initialized by sentence: " + sentence);
	}

	public Interpretation(String sentence, Interpretation interpretation) {
		this.put(interpretation);
		this.sentenceType = SentenceType.STATEMENT;
		this.sentence = sentence;
        LOGGER.debug("Interpretation initialized by sentence: " + sentence + " and Interpretation: " + interpretation);
	}

//	public Interpretation(SentenceType sentenceType){
//		this.sentenceType = sentenceType;
//	}

	public Interpretation(SentenceType sentenceType, String sentence, Triple triple){
		this.sentenceType = sentenceType;
		this.sentence = sentence;
		this.triples = new ArrayList<>();
		this.triples.add(triple);
        LOGGER.debug("Interpretation initialized by sentence: " + sentence + "" +
                ", sentence type: " + sentenceType + " and triple: " + triple.toString());
	}

	public Interpretation(Interpretation interpretation) {
	    this.copy(interpretation);
        LOGGER.debug("Interpretation initialized by Interpretation: " + interpretation);
    }

    @Nullable
	public SentenceType getSentenceType() {
		return sentenceType;
	}

	public void setSentenceType(SentenceType sentenceType) {
		this.sentenceType = sentenceType;
	}

    @Nullable
    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    @Nullable
    public List<Triple> getTriples() {
        return triples;
    }

    public void setTriples(List<Triple> triple) {
        this.triples = triple;
    }

    @Nullable
    public List<Triple> getSemTriples() {
        return semTriples;
    }

    public void setSemTriples(List<Triple> semTriples) {
        this.semTriples = semTriples;
        this.semTriples.removeIf(Objects::isNull);
    }

    @Nullable
    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
        //this.tokens.removeIf(Objects::isNull);
    }

    @Nullable
    public String[] getPosTags() {
        return posTags;
    }

    public void setPosTags(String[] posTags) {
        this.posTags = posTags;
    }

    @Nullable
    public String[] getLemmas() {
        return lemmas;
    }

    public void setLemmas(String[] lemmas) {
        this.lemmas = lemmas;
    }

    @Nullable
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

    @Nullable
    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    @Nullable
    public Map<SemanticRole, String> getPas() {
        return pas;
    }

    public void setPas(Map<SemanticRole, String> pas) {
        this.pas = pas;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
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

    @Nullable
    public String getObjAnswer() {
        return objAnswer;
    }

    public void setObjAnswer(String objAnswer) {
        this.objAnswer = objAnswer;
    }

    @Nullable
    public String getPredAnswer() {
        return predAnswer;
    }

    public void setPredAnswer(String predAnswer) {
        this.predAnswer = predAnswer;
    }

    @Nullable
    public RoboyEmotion getEmotion() {
        return emotion;
    }

    public void setEmotion(RoboyEmotion emotion) {
        this.emotion = emotion;
    }

    public boolean getProfanity() { return profanity; }

	public void setProfanity(boolean profanity) { this.profanity = profanity; }

    @Nullable
    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    @Nullable
    public String getIntentDistance() {
        return intentDistance;
    }

    public void setIntentDistance(String intentDistance) {
        this.intentDistance = intentDistance;
    }

    @Nullable
    public String getParse() {
        return parse;
    }

    public void setParse(String parse) {
        this.parse = parse;
    }

    @Nullable
    public String getParseAnswer() {
        return parseAnswer;
    }

    public void setParseAnswer(String parseAnswer) {
        this.parseAnswer = parseAnswer;
    }

    @Nullable
    public String getUnderspecifiedTermQuestion() {
        return underspecifiedTermQuestion;
    }

    public void setUnderspecifiedTermQuestion(String underspecifiedTermQuestion) {
        this.underspecifiedTermQuestion = underspecifiedTermQuestion;
    }

    @Nullable
    public String getUnderspecifiedQuestion() {
        return underspecifiedQuestion;
    }

    public void setUnderspecifiedQuestion(String underspecifiedQuestion) {
        this.underspecifiedQuestion = underspecifiedQuestion;
    }

    @Nullable
    public String getUnderspecifiedAnswer() {
        return underspecifiedAnswer;
    }

    public void setUnderspecifiedAnswer(String underspecifiedAnswer) {
        this.underspecifiedAnswer = underspecifiedAnswer;
    }

    @Nullable
    public UtteranceSentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(UtteranceSentiment sentiment) {
        this.sentiment = sentiment;
    }

    @Nullable
    public String getUtteranceType() {
        return utteranceType;
    }

    public void setUtteranceType(String utteranceType) {
        this.utteranceType = utteranceType;
    }

    @Nullable
    public ParsingOutcome getParsingOutcome() {
        return parsingOutcome;
    }

    public void setParsingOutcome(ParsingOutcome parsingOutcome) {
        this.parsingOutcome = parsingOutcome;
    }

    @Nullable
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

    // TODO the method copies the fields from the
    public void copy(Interpretation interpretation) {
        if (interpretation != null) {
            this.sentenceType = interpretation.getSentenceType();
            this.sentence = interpretation.getSentence();
            this.triples = interpretation.getTriples();
            this.semTriples = interpretation.getSemTriples();
            this.tokens = interpretation.getTokens();
            this.posTags = interpretation.getPosTags();
            this.lemmas = interpretation.getLemmas();
            this.keywords = interpretation.getKeywords();
            this.association = interpretation.getAssociation();
            this.pas = interpretation.getPas();
            this.name = interpretation.getName();
            this.celebrity = interpretation.getCelebrity();
            this.isRoboy = interpretation.isRoboy();
            this.objAnswer = interpretation.getObjAnswer();
            this.predAnswer = interpretation.getPredAnswer();
            this.emotion = interpretation.getEmotion();
            this.intent = interpretation.getIntent();
            this.intentDistance = interpretation.getIntentDistance();
            this.parse = interpretation.getParse();
            this.parseAnswer = interpretation.getParseAnswer();
            this.underspecifiedTermQuestion = interpretation.getUnderspecifiedTermQuestion();
            this.underspecifiedQuestion = interpretation.getUnderspecifiedQuestion();
            this.underspecifiedAnswer = interpretation.getUnderspecifiedAnswer();
            this.sentiment = interpretation.getSentiment();
            this.utteranceType = interpretation.getUtteranceType();
            this.parsingOutcome = interpretation.getParsingOutcome();
        }
//        Field[] fields = interpretation.getClass().getFields();
//        Method[] localMethods = this.getClass().getMethods();
//        for (Method m : interpretation.getClass().getMethods())
//            if (m.getName().startsWith("get") && m.getParameterTypes().length == 0) {
//                Object r = m.invoke(interpretation);
//                Class<?> type = m.getReturnType();
//                for (Method localMethod : localMethods) {
//                    if (localMethod.getName().startsWith("set") && localMethod.getName().endsWith(m.getName().substring(3))) {
//                        localMethod.invoke(this, r);
//                    }
//                }
//                // do your thing with r
//            }
    }

    public void put(Interpretation interpretation) {
	    if (interpretation != null) {
            if (interpretation.getSentenceType() != null) {
                this.sentenceType = interpretation.getSentenceType();
            }
            if (interpretation.getSentence() != null) {
                this.sentence = interpretation.getSentence();
            }
            if (interpretation.getTriples() != null) {
                this.triples = interpretation.getTriples();
            }
            if (interpretation.getSemTriples() != null) {
                this.semTriples = interpretation.getSemTriples();
            }
            if (interpretation.getTokens() != null) {
                this.tokens = interpretation.getTokens();
            }
            if (interpretation.getPosTags() != null) {
                this.posTags = interpretation.getPosTags();
            }
            if (interpretation.getLemmas() != null) {
                this.lemmas = interpretation.getLemmas();
            }
            if (interpretation.getKeywords() != null) {
                this.keywords = interpretation.getKeywords();
            }
            if (interpretation.getAssociation() != null) {
                this.association = interpretation.getAssociation();
            }
            if (interpretation.getPas() != null) {
                this.pas = interpretation.getPas();
            }
            if (interpretation.getName() != null) {
                this.name = interpretation.getName();
            }
            if (interpretation.getCelebrity() != null) {
                this.celebrity = interpretation.getCelebrity();
            }
            if (interpretation.getObjAnswer() != null) {
                this.objAnswer = interpretation.getObjAnswer();
            }
            if (interpretation.getPredAnswer() != null) {
                this.predAnswer = interpretation.getPredAnswer();
            }
            if (interpretation.getEmotion() != null) {
                this.emotion = interpretation.getEmotion();
            }
            if (interpretation.getIntent() != null) {
                this.intent = interpretation.getIntent();
            }
            if (interpretation.getIntentDistance() != null) {
                this.intentDistance = interpretation.getIntentDistance();
            }
            if (interpretation.getParse() != null) {
                this.parse = interpretation.getParse();
            }
            if (interpretation.getParseAnswer() != null) {
                this.parseAnswer = interpretation.getParseAnswer();
            }
            if (interpretation.getUnderspecifiedTermQuestion() != null) {
                this.underspecifiedTermQuestion = interpretation.getUnderspecifiedTermQuestion();
            }
            if (interpretation.getUnderspecifiedQuestion() != null) {
                this.underspecifiedQuestion = interpretation.getUnderspecifiedQuestion();
            }
            if (interpretation.getUnderspecifiedAnswer() != null) {
                this.underspecifiedAnswer = interpretation.getUnderspecifiedAnswer();
            }
            if (interpretation.getSentiment() != null) {
                this.sentiment = interpretation.getSentiment();
            }
            if (interpretation.getUtteranceType() != null) {
                this.utteranceType = interpretation.getUtteranceType();
            }
            if (interpretation.getParsingOutcome() != null) {
                this.parsingOutcome = interpretation.getParsingOutcome();
            }
            this.isRoboy = interpretation.isRoboy();
        }
    }

    @Override
    public String toString() {
        return "Interpretation{" +
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
                ", parsingOutcome=" + parsingOutcome +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Interpretation comparableObject = (Interpretation) obj;
        return isRoboy() == comparableObject.isRoboy() &&
                getIntentDistance() == comparableObject.getIntentDistance() &&
                getSentenceType() == comparableObject.getSentenceType() &&
                Objects.equals(getSentence(), comparableObject.getSentence()) &&
                Objects.equals(getTriples(), comparableObject.getTriples()) &&
                Objects.equals(getSemTriples(), comparableObject.getSemTriples()) &&
                Objects.equals(getTokens(), comparableObject.getTokens()) &&
                Arrays.equals(getPosTags(), comparableObject.getPosTags()) &&
                Arrays.equals(getLemmas(), comparableObject.getLemmas()) &&
                Objects.equals(getKeywords(), comparableObject.getKeywords()) &&
                Objects.equals(getAssociation(), comparableObject.getAssociation()) &&
                Objects.equals(getPas(), comparableObject.getPas()) &&
                Objects.equals(getName(), comparableObject.getName()) &&
                Objects.equals(getCelebrity(), comparableObject.getCelebrity()) &&
                Objects.equals(getObjAnswer(), comparableObject.getObjAnswer()) &&
                Objects.equals(getPredAnswer(), comparableObject.getPredAnswer()) &&
                getEmotion() == comparableObject.getEmotion() &&
                Objects.equals(getIntent(), comparableObject.getIntent()) &&
                Objects.equals(getParse(), comparableObject.getParse()) &&
                Objects.equals(getParseAnswer(), comparableObject.getParseAnswer()) &&
                Objects.equals(getUnderspecifiedTermQuestion(), comparableObject.getUnderspecifiedTermQuestion()) &&
                Objects.equals(getUnderspecifiedQuestion(), comparableObject.getUnderspecifiedQuestion()) &&
                Objects.equals(getUnderspecifiedAnswer(), comparableObject.getUnderspecifiedAnswer()) &&
                getSentiment() == comparableObject.getSentiment() &&
                Objects.equals(getUtteranceType(), comparableObject.getUtteranceType()) &&
                getParsingOutcome() == comparableObject.getParsingOutcome() &&
                Objects.equals(getAnswer(), comparableObject.getAnswer());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getSentenceType(), getSentence(), getTriples(), getSemTriples(), getTokens(),
                getKeywords(), getAssociation(), getPas(), getName(), getCelebrity(), isRoboy(), getObjAnswer(),
                getPredAnswer(), getEmotion(), getIntent(), getIntentDistance(), getParse(), getParseAnswer(),
                getUnderspecifiedTermQuestion(), getUnderspecifiedQuestion(), getUnderspecifiedAnswer(), getSentiment(),
                getUtteranceType(), getParsingOutcome(), getAnswer());
        result = 31 * result + Arrays.hashCode(getPosTags());
        result = 31 * result + Arrays.hashCode(getLemmas());
        return result;
    }
}
