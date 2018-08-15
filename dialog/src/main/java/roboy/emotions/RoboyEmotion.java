package roboy.emotions;

/**
 * Comprises the emotions Roboy can demonstrate
 */
public enum RoboyEmotion {
    SHY("shy"),
    SMILE_BLINK("smileblink"),
    LOOK_LEFT("lookleft"),
    LOOK_RIGHT("lookright"),
    CAT_EYES("catiris"),
    KISS("kiss"),
    FACEBOOK_EYES("img:facebook"),
    NEUTRAL("neutral"),
    HAPPINESS("happiness"),
    SADNESS("sadness"),
    BEER_THIRSTY("beer");

    public String type;

    RoboyEmotion(String type) {
        this.type = type;
    }
}
