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
    private final List<String> dropout;
    public EmotionAnalyzer(){
        Word2Vec vec = Word2vec.getInstance().getModel();
        beerVec = vec.getWordVector("beer");
        happyVec = vec.getWordVector("happy");
        sadVec = vec.getWordVector("sad");

        String[] dropList = new String[]{
                "TO","CC", "CD", "DT", "EX", "PRP", "WDT","WP", "PDT"
        };

        dropout = new ArrayList<String>(Arrays.asList(dropList));
    }

    public Interpretation analyze(Interpretation interpretation)
    {
        List<String> tokens = interpretation.getTokens();
        String[] tags = interpretation.getPosTags();
        LOGGER.error("tags.length: "+String.valueOf(tags.length));
        LOGGER.error("tokens.size: "+String.valueOf(tokens.size()));

        if(tokens == null){
            LOGGER.error("TOKENS ARE NULL - It is impossible");
            return interpretation;
        }

        List<String> labels = new ArrayList<>();
        for(int i = 0; i<tokens.size(); i++){
            String token = tokens.get(i);
            LOGGER.error("token: "+token);
            //if token has a label that is not desired do not add.
            if(!dropout.contains(tags[i])){
                LOGGER.error("not droped out token: "+token);
                labels.add(token);
            }

        }

        Word2Vec vec = Word2vec.getInstance().getModel();
        INDArray mean = vec.getWordVectorsMean(labels);
        if(!mean.isVector()){
            //if mean is not a vector just return without emotion
            LOGGER.error("MEAN IS NOT A VECTOR");
            return interpretation;
        }

        double[] dMean = mean.data().asDouble();
        boolean sentencePositive = false;
        if(interpretation.getSentiment() != null){
            sentencePositive = interpretation.getSentiment().equals(Linguistics.UtteranceSentiment.POSITIVE);
        }


        double beerSimilarity = cosineSimilarity(dMean, beerVec);
        double sadSimilarity = cosineSimilarity(dMean, sadVec);
        double happySimilarity = cosineSimilarity(dMean, happyVec);

        LOGGER.error("[dropout]beerSimilarity: "+String.valueOf(beerSimilarity));
        LOGGER.error("[dropout]sadSimilarity: "+String.valueOf(sadSimilarity));
        LOGGER.error("[dropout]happySimilarity: "+String.valueOf(happySimilarity));

        mean = vec.getWordVectorsMean(tokens);
        dMean = mean.data().asDouble();

        beerSimilarity = cosineSimilarity(dMean, beerVec);
        sadSimilarity = cosineSimilarity(dMean, sadVec);
        happySimilarity = cosineSimilarity(dMean, happyVec);

        LOGGER.error("[no dropout]beerSimilarity: "+String.valueOf(beerSimilarity));
        LOGGER.error("[no dropout]sadSimilarity: "+String.valueOf(sadSimilarity));
        LOGGER.error("[no dropout]happySimilarity: "+String.valueOf(happySimilarity));

        if(beerSimilarity >= threshold){
            interpretation.setEmotion(RoboyEmotion.BEER_THIRSTY);
        }
        else if(sadSimilarity >= threshold){
            interpretation.setEmotion(RoboyEmotion.SADNESS);
        }
        else if(happySimilarity >= threshold){
            if(sentencePositive){
                interpretation.setEmotion(RoboyEmotion.HAPPINESS);
            }
        }

        interpretation.setEmotion(RoboyEmotion.NEUTRAL);
        return interpretation;

//        //TODO: to be deleted
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
