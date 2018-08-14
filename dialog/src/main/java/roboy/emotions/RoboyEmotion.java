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
    INSTAGRAM_EYES("img:instagram"),
    LINKED_IN_EYES("img:linkedin"),
    NEUTRAL("neutral"),
    CRY("cry"),
    ANGRY("angry"),
    PISSED("pissed"),
    ANNOYED("annoyed"),
    ROLLING_EYES("rolling"),
    HYPNO_EYES("hypno"),
    HYPNO_COLOUR_EYES("hypno_color"),
    GLASSES("glasses"),
    MOUSTACHE("moustache"),
    PIRATE("pirate"),
    SUNGLASSES("sunglasses"),
    SURPRISED("suprised"),
    HAPPY("happy"),
    TONGUE("tongue"),
    TEETH("teeth"),
    HEARTS("hearts");

    public String type;

    RoboyEmotion(String type) {
        this.type = type;
    }

}
