package roboy.parser;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import roboy.memory.Neo4jRelationship;
import roboy.util.JsonEntryModel;
import roboy.util.QAJsonParser;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class QAParserTest {
    static QAJsonParser parser;
    static String path = "test.json";

    @BeforeClass
    public static void createJsonAndParse() {
        File file = new File(path);
        try {
            Writer writer = new BufferedWriter(new FileWriter(file));
            String contents = "{\n" +
                    "  \"IS\": {\n" +
                    "    \"Q\": [\n" +
                    "      \"TEST_QUESTION\",\n" +
                    "    ],\n" +
                    "    \"A\": {\n" +
                    "      \"SUCCESS\": [\n" +
                    "        \"TEST_SUCCESS %s\"\n" +
                    "      ],\n" +
                    "      \"FAILURE\": [\n" +
                    "        \"TEST_FAILURE\"\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    \"FUP\": {\n" +
                    "      \"Q\": [\n" +
                    "        \"TEST_FUP_QUESTION %s\"\n" +
                    "      ],\n" +
                    "      \"A\": [\n" +
                    "        \"TEST_FUP_ANSWER %s\"\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            writer.write(contents);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        parser = new QAJsonParser(path);
    }

    @Test
    public void testEntryEquivalency() {
        // TODO: Check entry equivalence with mock class
        JsonEntryModel IS = new JsonEntryModel();
    }

    @Test
    public void testQuestions() {
        assertEquals("TEST_QUESTION", parser.getEntry(Neo4jRelationship.IS).getQuestions().get(0));
        assertEquals("TEST_QUESTION", parser.getQuestions(Neo4jRelationship.IS).get(0));
    }

    @Test
    public void testAnswers() {
        assertEquals("TEST_SUCCESS %s", parser.getEntry(Neo4jRelationship.IS).getAnswers().get("SUCCESS").get(0));
        assertEquals("TEST_FAILURE", parser.getEntry(Neo4jRelationship.IS).getAnswers().get("FAILURE").get(0));
        assertEquals("TEST_SUCCESS %s", parser.getAnswers(Neo4jRelationship.IS).get("SUCCESS").get(0));
        assertEquals("TEST_FAILURE", parser.getAnswers(Neo4jRelationship.IS).get("FAILURE").get(0));
        assertEquals("TEST_SUCCESS %s", parser.getSuccessAnswers(Neo4jRelationship.IS).get(0));
        assertEquals("TEST_FAILURE", parser.getFailureAnswers(Neo4jRelationship.IS).get(0));
    }

    @Test
    public void testFollowUp() {
        assertEquals("TEST_FUP_QUESTION %s", parser.getEntry(Neo4jRelationship.IS).getFUP().get("Q").get(0));
        assertEquals("TEST_FUP_ANSWER %s", parser.getEntry(Neo4jRelationship.IS).getFUP().get("A").get(0));
        assertEquals("TEST_FUP_QUESTION %s", parser.getFollowUp(Neo4jRelationship.IS).get("Q").get(0));
        assertEquals("TEST_FUP_ANSWER %s", parser.getFollowUp(Neo4jRelationship.IS).get("A").get(0));
        assertEquals("TEST_FUP_QUESTION %s", parser.getFollowUpQuestions(Neo4jRelationship.IS).get(0));
        assertEquals("TEST_FUP_ANSWER %s", parser.getFollowUpAnswers(Neo4jRelationship.IS).get(0));
    }

    @AfterClass
    public static void cleanUpJson() {
        try{
            File file = new File(path);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println(file.getName() + " is deleted!");
                } else {
                    System.out.println("Test cleanup failed. Couldn't remove " + file.getName());
                }
            } else {
                System.out.println("Test cleanup failed. File doesn't exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
