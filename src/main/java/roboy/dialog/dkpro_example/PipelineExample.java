package roboy.dialog.dkpro_example;
import de.tudarmstadt.ukp.dkpro.core.io.conll.Conll2006Writer;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.maltparser.MaltParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
//import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpSemanticRoleLabeler;
//import de.tudarmstadt.ukp.dkpro.core.matetools.MateSemanticRoleLabeler;
//import com.carrotsearch.hppc.ObjectIntOpenHashMap;

/**
 * @author Laura Lahesoo
 *
 * This is a linguistic analysis pipeline example from:
 * https://dkpro.github.io/dkpro-core/pages/java-intro/
 *
 * If you wish to test the pipeline, write your input into the file document.txt
 * The output will be written in document.txt.conll
 *
 * If you wish to continue integrating SRL tools, you may start where I left off by
 * uncommenting the semantic role labeler dependencies (and hashMap due to some
 * interdependency) here and in the pom file. At the time of writing, neither of
 * the labelers worked: ClearNlp SRL does not produce output and MateSRL throws a
 * ClassNotFoundException - possibly due to some dependency issues.
 *
 */

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

public class PipelineExample {

    public static void main(String[] args) throws Exception {
        runPipeline(
                createReaderDescription(TextReader.class,
                        TextReader.PARAM_SOURCE_LOCATION, "src/main/java/roboy/dialog/dkpro_example/document.txt",
                        TextReader.PARAM_LANGUAGE, "en"),
                createEngineDescription(OpenNlpSegmenter.class),
                createEngineDescription(OpenNlpPosTagger.class),
                createEngineDescription(LanguageToolLemmatizer.class),
                createEngineDescription(MaltParser.class),
                //createEngineDescription(ClearNlpSemanticRoleLabeler.class),
                //createEngineDescription(MateSemanticRoleLabeler.class),
                createEngineDescription(Conll2006Writer.class,
                        Conll2006Writer.PARAM_TARGET_LOCATION, "src/main/java/roboy/dialog/dkpro_example/."));
    }
}