package roboy.dialog.personality.experimental.helpers;

import roboy.dialog.personality.experimental.DialogStateMachine;
import roboy.dialog.personality.experimental.toyStates.*;

import java.io.File;

/**
 * This class provides examples how to load state machines from files
 * or create them from code directly.
 */
public class StateMachineExamples {

    public static void main(String[] args) throws Exception {
        // create all states and set all connections from code directly
        DialogStateMachine code = fromCode();

        // load states and connections from file
        DialogStateMachine file = fromFile();

        // load states and connections from string (not readable, mainly used for unit tests)
        DialogStateMachine string = fromString();

        System.out.println(file);

        System.out.println("JSON representation:");
        System.out.println(file.toJsonString());


        System.out.println("Dialog machine from code, file and string are equal: "
                + ( code.equals(file)   &&
                    code.equals(string) &&
                    string.equals(code) &&
                    string.equals(file) &&
                    file.equals(string) &&
                    file.equals(code)
                )
        );


        System.out.println("Saving to resources/personalityFiles/ExamplePersonality2.json");
        file.saveToFile(new File ("resources/personalityFiles/ExamplePersonality2.json"));

    }

    private static DialogStateMachine fromCode() {

        // create states
        ToyGreetingsState greetings = new ToyGreetingsState("Greetings");
        ToyIntroState intro = new ToyIntroState("Intro");
        ToyFarewellState farewell = new ToyFarewellState("Farewell");
        ToyRandomAnswerState randomAnswer = new ToyRandomAnswerState("RandomAnswer");

        // set fallbacks and transitions
        greetings.setFallback(randomAnswer);
        greetings.setTransition("next", intro);
        greetings.setTransition("noHello", farewell);
        intro.setTransition("next", farewell);
        randomAnswer.setTransition("next", farewell);

        // create the dialog machine an register states
        DialogStateMachine stateMachine = new DialogStateMachine();
        stateMachine.addState(greetings);
        stateMachine.addState(intro);
        stateMachine.addState(farewell);
        stateMachine.addState(randomAnswer);
        stateMachine.setInitialState(greetings);

        return stateMachine;

    }

    private static DialogStateMachine fromFile() throws Exception {
        DialogStateMachine stateMachine = new DialogStateMachine();
        stateMachine.loadFromFile(new File("resources/personalityFiles/ExamplePersonality.json"));
        return stateMachine;
    }

    private static DialogStateMachine fromString() {
        DialogStateMachine stateMachine = new DialogStateMachine();
        stateMachine.loadFromString(toyPersonality);
        return stateMachine;
    }



    private static final String toyPersonality = "{\n" +
            "  \"initialState\": \"Greetings\",\n" +
            "  \"states\": [\n" +
            "    {\n" +
            "      \"identifier\": \"Greetings\",\n" +
            "      \"implementation\" : \"roboy.dialog.personality.experimental.toyStates.ToyGreetingsState\",\n" +
            "      \"fallback\" : \"RandomAnswer\",\n" +
            "      \"transitions\" : {\n" +
            "        \"next\" : \"Intro\",\n" +
            "        \"noHello\" : \"Farewell\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"identifier\": \"Intro\",\n" +
            "      \"implementation\" : \"roboy.dialog.personality.experimental.toyStates.ToyIntroState\",\n" +
            "      \"fallback\" : null,\n" +
            "      \"transitions\" : {\n" +
            "        \"next\" : \"Farewell\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"identifier\": \"Farewell\",\n" +
            "      \"implementation\" : \"roboy.dialog.personality.experimental.toyStates.ToyFarewellState\",\n" +
            "      \"fallback\" : null,\n" +
            "      \"transitions\" : {}\n" +
            "    },\n" +
            "    {\n" +
            "      \"identifier\": \"RandomAnswer\",\n" +
            "      \"implementation\" : \"roboy.dialog.personality.experimental.toyStates.ToyRandomAnswerState\",\n" +
            "      \"fallback\" : null,\n" +
            "      \"transitions\" : {\n" +
            "        \"next\" : \"Farewell\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";


}
