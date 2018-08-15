package roboy.linguistics.sentenceanalysis;

import edu.stanford.nlp.sempre.roboy.lexicons.word2vec.Word2vec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import roboy.emotions.RoboyEmotion;
import roboy.linguistics.Keyword;
import roboy.linguistics.Entity;
import roboy.linguistics.Linguistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Checks for a handfull of keywords and stores more or less fitting emotions
 * in the Linguistics.EMOTION feature that is later read out and fed to the
 * facial expression output module.
 */
public class EmotionAnalyzer implements Analyzer {

    private final double threshold = 0.5;
    private final Logger LOGGER = LogManager.getLogger();

    private final double[] beerVec;
    private final double[] sadVec;
    private final double[] happyVec;
    private final double[] shyVec;
    private final List<String> dropout;
    public EmotionAnalyzer(){
        Word2Vec vec = Word2vec.getInstance().getModel();
        beerVec = vec.getWordVector("beer");
        happyVec = vec.getWordVector("happy");
        sadVec = vec.getWordVector("sad");

        List<String> shyList = new ArrayList<>();
        shyList.add("love");
        shyList.add("kiss");
        shyVec = vec.getWordVectorsMean(shyList).data().asDouble();

        String[] dropList = new String[]{
                "TO","CC", "CD", "DT", "EX", "PRP", "WDT","WP", "PDT"
        };

        dropout = new ArrayList<String>(Arrays.asList(dropList));
    }

    public Interpretation analyze(Interpretation interpretation)
    {
        List<String> tokens = interpretation.getTokens();
        String[] tags = interpretation.getPosTags();
        LOGGER.debug("tags.length: "+String.valueOf(tags.length));
        LOGGER.debug("tokens.size: "+String.valueOf(tokens.size()));

        if(tokens == null){
            LOGGER.debug("TOKENS ARE NULL - It is impossible");
            interpretation.setEmotion(RoboyEmotion.NEUTRAL);
            return interpretation;
        }

        List<String> labels = new ArrayList<>();
        for(int i = 0; i<tokens.size(); i++){
            String token = tokens.get(i);
            LOGGER.error("token: "+token);
            //if token has a label that is not desired do not add.
            if(!dropout.contains(tags[i])){
                LOGGER.debug("not droped out token: "+token);
                labels.add(token);
            }

        }

        Word2Vec vec = Word2vec.getInstance().getModel();

        INDArray mean;
        try{
            mean = vec.getWordVectorsMean(labels);
        }catch (Exception e){
            interpretation.setEmotion(RoboyEmotion.NEUTRAL);
            return interpretation;
        }

        if(mean == null || !mean.isVector()){
            //if mean is not a vector just return without emotion
            interpretation.setEmotion(RoboyEmotion.NEUTRAL);
            return interpretation;
        }

        double[] dMean = mean.data().asDouble();
        boolean sentencePositive = false;
        boolean sentenceNotNegative = false; // not the same with positive
        if(interpretation.getSentiment() != null){
            sentencePositive = interpretation.getSentiment().equals(Linguistics.UtteranceSentiment.POSITIVE);
            sentenceNotNegative = !interpretation.getSentiment().equals(Linguistics.UtteranceSentiment.NEGATIVE);
        }


        double beerSimilarity = cosineSimilarity(dMean, beerVec);
        double sadSimilarity = cosineSimilarity(dMean, sadVec);
        double happySimilarity = cosineSimilarity(dMean, happyVec);
        double shySimilarity = cosineSimilarity(dMean, shyVec);

        mean = vec.getWordVectorsMean(tokens);
        dMean = mean.data().asDouble();

        //if you need the similarity without the dropout
        double _beerSimilarity = cosineSimilarity(dMean, beerVec);
        double _sadSimilarity = cosineSimilarity(dMean, sadVec);
        double _happySimilarity = cosineSimilarity(dMean, happyVec);
        double _shySimilarity = cosineSimilarity(dMean, shyVec);

        if(beerSimilarity >= threshold){
            if(sentenceNotNegative){
                interpretation.setEmotion(RoboyEmotion.BEER_THIRSTY);
            }
        }
        else if(sadSimilarity >= threshold){
            if(sentenceNotNegative){
                interpretation.setEmotion(RoboyEmotion.SADNESS);
            }
        }
        else if(happySimilarity >= threshold){
            if(sentencePositive){
                interpretation.setEmotion(RoboyEmotion.HAPPINESS);
            }

        }else if(shySimilarity >= threshold){
            if(sentenceNotNegative){
                interpretation.setEmotion(RoboyEmotion.SHY);
            }
        }
        else{
            interpretation.setEmotion(RoboyEmotion.NEUTRAL);
        }

        return interpretation;

//        // ------------ old emotion analyzer ------------
//        List<String> tokens = interpretation.getTokens();
//        if (tokens != null && !tokens.isEmpty()) {
//            if (tokens.contains("love") || tokens.contains("cute")) {
//                interpretation.setEmotion(RoboyEmotion.SHY);
//            } else if (tokens.contains("munich") || tokens.contains("robotics")) {
//                interpretation.setEmotion(RoboyEmotion.SMILE_BLINK);
//            } else if (tokens.contains("left")) {
//                interpretation.setEmotion(RoboyEmotion.LOOK_LEFT);
//            } else if (tokens.contains("right")) {
//                interpretation.setEmotion(RoboyEmotion.LOOK_RIGHT);
//            } else if (tokens.contains("cat") || tokens.contains("cats")) {
//                interpretation.setEmotion(RoboyEmotion.CAT_EYES);
//            } else {
//                interpretation.setEmotion(RoboyEmotion.NEUTRAL);
//            }
//
//            if (interpretation.isRoboy()) {
//                interpretation.setEmotion(RoboyEmotion.SMILE_BLINK);
//            }
//        }
//        return interpretation;
    }

    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
