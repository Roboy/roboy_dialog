package roboy.dialog.personality.experimental.helpers;

import java.io.File;

public class ExperimentalMain {

    public static void main(String[] args) throws Exception {
        PersonalityImpl personality = new PersonalityImpl();

        personality.loadStateMachine(
                new File("resources/personalityFiles/ExamplePersonality.json"));

        System.out.println(personality);

    }

}
