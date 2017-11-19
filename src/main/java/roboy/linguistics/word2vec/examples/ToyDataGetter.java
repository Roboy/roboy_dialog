package roboy.linguistics.word2vec.examples;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Utility class to load toy data from the internet if necessary.
 * May be refactored into something bigger and more useful later.
 */
public class ToyDataGetter {

    private final boolean verbose;
    private final String toyDataDirectory = "./resources/word2vec_toy_data_and_model/";
    private final String toyDataFilePath = "./resources/word2vec_toy_data_and_model/raw_sentences.txt";
    private final String toyDataInetURL = "https://raw.githubusercontent.com/deeplearning4j/dl4j-examples/master/dl4j-examples/src/main/resources/raw_sentences.txt";


    public ToyDataGetter(boolean verbose) {
        this.verbose = verbose;
    }


    public String getToyDataFilePath() {
        return toyDataFilePath;
    }

    /**
     * Checks if toy data is present on the hard drive. It will be downloaded if necessary.
     */
    public void ensureToyDataIsPresent() {

        // check if already downloaded
        if (fileExists(toyDataFilePath)) {
            if (verbose) System.out.println("Found data file (" + toyDataFilePath + ")");
            return;
        }

        // need to download
        try {
            if (verbose) System.out.println("Data file is missing and will be downloaded to " + toyDataFilePath);

            // make sure directory exists
            File dir = new File(toyDataDirectory);
            dir.mkdirs();

            downloadData(toyDataInetURL, toyDataFilePath);

        } catch (IOException e) {
            System.err.println("Sorry, couldn't download toy data! Exception: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
        }

    }


    private void downloadData(String fromURL, String toFilePath) throws IOException {
        URL website = new URL(fromURL);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(toFilePath);
        long bytesTransferred = fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        if (verbose) {
            System.out.println("Download complete, saved " + bytesTransferred + " bytes to " + toFilePath);
        }

    }

    private boolean fileExists(String filePath) {
        File data = new File(filePath);
        return data.exists();
    }

}
