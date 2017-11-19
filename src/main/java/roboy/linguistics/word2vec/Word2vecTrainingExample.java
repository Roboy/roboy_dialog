package roboy.linguistics.word2vec;

public class Word2vecTrainingExample {

    public static void main(String[] args) {

        ToyDataGetter dataGetter = new ToyDataGetter(true);
        dataGetter.ensureToyDataIsPresent();

        String dataPath = dataGetter.getToyDataFilePath();


    }


}
