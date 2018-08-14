package roboy.dialog.tutorials;

import roboy.context.Context;
import roboy.dialog.DialogStateMachine;
import roboy.dialog.states.definitions.StateParameters;
import roboy.dialog.tutorials.tutorialStates.*;
import roboy.logic.Inference;
import roboy.logic.InferenceEngine;

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

        boolean allEqual =  code.equals(file)   &&
                            code.equals(string) &&
                            string.equals(code) &&
                            string.equals(file) &&
                            file.equals(string) &&
                            file.equals(code);

        System.out.println("Dialog machine from code, file and string are equal: " + allEqual);
        if (! allEqual) {
            System.out.println("code.equals(file):   " + code.equals(file)   );
            System.out.println("code.equals(string): " + code.equals(string) );
            System.out.println("string.equals(code): " + string.equals(code) );
            System.out.println("string.equals(file): " + string.equals(file) );
            System.out.println("file.equals(string): " + file.equals(string) );
            System.out.println("file.equals(code):   " + file.equals(code)   );
        }

        //System.out.println("Saving to resources/personalityFiles/ExamplePersonality2.json");
        //file.saveToFile(new File ("resources/personalityFiles/ExamplePersonality2.json"));

    }

    private static DialogStateMachine fromCode() {

        // 0. Create an InferenceEngine and a Context (which is not necessary for a state machine per se but for most of the roboy states)
        InferenceEngine inference = new Inference();
        Context context = new Context();
        // 1. create the dialog machine
        DialogStateMachine stateMachine = new DialogStateMachine(inference, context);

        // 2. create states

        // states with no specific parameters -> one StateParameters object that is shared by all states
        StateParameters emptyParams = new StateParameters(stateMachine);
        ToyGreetingsState greetings = new ToyGreetingsState("Greetings", emptyParams);
        ToyFarewellState farewell = new ToyFarewellState("Farewell", emptyParams);
        ToyRandomAnswerState randomAnswer = new ToyRandomAnswerState("RandomAnswer", emptyParams);

        // states that require specific parameters -> one new StateParameters object for every state
        StateParameters introParams = new StateParameters(stateMachine);
        introParams.setParameter("introductionSentence", "This dialog was created from code");
        ToyIntroState intro = new ToyIntroState("Intro", introParams);

        // 3. set fallbacks and transitions
        greetings.setFallback(randomAnswer);
        greetings.setTransition("next", intro);
        greetings.setTransition("noHello", farewell);
        intro.setTransition("next", farewell);
        randomAnswer.setTransition("next", farewell);

        // 4. register states in in the state machine: this doesn't happen automatically!
        stateMachine.addState(greetings);
        stateMachine.addState(intro);
        stateMachine.addState(randomAnswer);
        stateMachine.addState(farewell);

        // 5. define initial state
        stateMachine.setInitialState(greetings);


        return stateMachine;

    }

    private static DialogStateMachine fromFile() throws Exception {
        InferenceEngine inference = new Inference();
        Context context = new Context();
        DialogStateMachine stateMachine = new DialogStateMachine(inference, context);
        stateMachine.loadFromFile(new File("resources/personalityFiles/tutorial/ToyStateMachine.json"));
        return stateMachine;
    }

    private static DialogStateMachine fromString() {
        InferenceEngine inference = new Inference();
        Context context = new Context();
        DialogStateMachine stateMachine = new DialogStateMachine(inference, context);
        stateMachine.loadFromString(toyPersonality);
        return stateMachine;
    }



    private static final String toyPersonality = "{\n" +
            "  \"initialState\": \"Greetings\",\n" +
            "  \"states\": [\n" +
            "    {\n" +
            "      \"identifier\": \"Greetings\",\n" +
            "      \"implementation\" : \"roboy.dialog.tutorials.tutorialStates.ToyGreetingsState\",\n" +
            "      \"fallback\" : \"RandomAnswer\",\n" +
            "      \"transitions\" : {\n" +
            "        \"next\" : \"Intro\",\n" +
            "        \"noHello\" : \"Farewell\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"identifier\": \"Intro\",\n" +
            "      \"implementation\" : \"roboy.dialog.tutorials.tutorialStates.ToyIntroState\",\n" +
            "      \"fallback\" : null,\n" +
            "      \"transitions\" : {\n" +
            "        \"next\" : \"Farewell\"\n" +
            "      },\n" +
            "      \"parameters\" : {\n" +
            "        \"introductionSentence\" : \"My name is Roboy!\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"identifier\": \"Farewell\",\n" +
            "      \"implementation\" : \"roboy.dialog.tutorials.tutorialStates.ToyFarewellState\",\n" +
            "      \"fallback\" : null,\n" +
            "      \"transitions\" : {}\n" +
            "    },\n" +
            "    {\n" +
            "      \"identifier\": \"RandomAnswer\",\n" +
            "      \"implementation\" : \"roboy.dialog.tutorials.tutorialStates.ToyRandomAnswerState\",\n" +
            "      \"fallback\" : null,\n" +
            "      \"transitions\" : {\n" +
            "        \"next\" : \"Farewell\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";


}
