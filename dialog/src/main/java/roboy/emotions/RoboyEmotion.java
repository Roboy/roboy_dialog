package roboy.emotions;


import roboy.util.RandomList;

/**
 * Comprises the emotions Roboy can demonstrate
 */
public enum RoboyEmotion {
    SHY("shy", "CAADAgADSwAD5dCAEBGmde8-twTLAg"),
    SMILE_BLINK("smileblink", "CAADAgADSgAD5dCAEMQakIa3aHHSAg"),
    LOOK_LEFT("lookleft", null),
    LOOK_RIGHT("lookright", null),
    CAT_EYES("catiris", null),
    KISS("kiss", "CAADAgADSAAD5dCAELAByOgU5LIwAg"),
    FACEBOOK_EYES("img:facebook", null),
    INSTAGRAM_EYES("img:instagram", null),
    LINKED_IN_EYES("img:linkedin", null),
    NEUTRAL("neutral", "CAADAgADFQAD5dCAEKM0TS8sjXiAAg"),
    CRY("cry", "CAADAgADiAAD5dCAEOQeh7anZFNuAg"),
    ANGRY("angry", null),
    PISSED("pissed", "CAADAgADgQAD5dCAEItX3SnG6CboAg"),
    ANNOYED("annoyed", "CAADAgADgQAD5dCAEItX3SnG6CboAg"),
    ROLLING_EYES("rolling", null),
    HYPNO_EYES("hypno", "CAADAgADcAAD5dCAEMJJxG1uTYH6Ag"),
    HYPNO_COLOUR_EYES("hypno_color", "CAADAgADcAAD5dCAEMJJxG1uTYH6Ag"),
    GLASSES("glasses", "CAADAgADvQAD5dCAECNJAAFZJ_lnKwI"),
    MOUSTACHE("moustache", null),
    PIRATE("pirate", "CAADAgADMwAD5dCAEDmHbbo1QxeWAg"),
    SUNGLASSES("sunglasses", "CAADAgADLwAD5dCAENTFuFLbW8-XAg"),
    SURPRISED("suprised", null),
    HAPPY("lucky", null),
    TONGUE("tongue", "CAADAgADUQAD5dCAEIT4-Rl1t2BEAg"),
    TEETH("teeth", null),
    HEARTS("hearts", "CAADAgADSQAD5dCAEN9n0g-x5va8Ag"),
    HAPPINESS("happiness", "CAADAgADRgAD5dCAEJV_o50ekE5HAg"),
    SADNESS("sadness", "CAADAgADTAAD5dCAENsDuDryjXuhAg"),
    BEER_THIRSTY("beer", "CAADAgADKQAD5dCAEFX3hCMAAfM_awI"),
    KENNY("kenny", "CAADAgADkAAD5dCAEGMfygavvZSZAg"),
    MIB("maninblack", "CAADAgADXQAD5dCAEJY_NKT6hMaOAg"),
    MINDBLOWN("mindblown","CAADAgADsgAD5dCAEBmMXCCt4Sh6Ag"),
    DANCE("dance", "CAADAgADrgAD5dCAEP7FI8ogeANNAg"),
    SUPERMAN("superman","CAADAgADoQAD5dCAEN7_d_TMkG8CAg"),
    PICKLEROBOY("neutral", "CAADAgADwgAD5dCAEKjfQCRuUDfYAg"),
    THUMBSUP("neutral","CAADAgADYAAD5dCAEIqcImOJTl-_Ag"),
    POSTCARDFACE("neutral","CAADAgADRQAD5dCAEE75Fhiqn2p-Ag"),
    ANGEL("neutral", "CAADAgADTQAD5dCAEDafVf7FGlynAg"),
    RAINBOW("neutral", "CAADAgADVQAD5dCAEHTBjm9cSbBTAg"),
    RAINBOWVR("neutral", "CAADAgADZgAD5dCAED-lBppglhuaAg"),
    BUNNY("neutral", "CAADAgADUgAD5dCAEJKbI9Fpaw5-Ag"),
    MONOCLE("neutral", "CAADAgADUwAD5dCAEG5fdActwqACAg");


    public String type;
    public String telegramID;

    RoboyEmotion(String type, String telegramID) {
        this.type = type;
        this.telegramID = telegramID;
    }

    public static RandomList<RoboyEmotion> winnerEmotions = new RandomList(
            DANCE,
            SUNGLASSES,
            SUPERMAN,
            KENNY,
            MONOCLE);

    public static RandomList<RoboyEmotion> loserEmotions = new RandomList(
            MIB,
            SADNESS,
            CRY);

    public static RandomList<RoboyEmotion> positive = new RandomList(
            SMILE_BLINK,
            NEUTRAL,
            TONGUE,
            HAPPINESS,
            KISS,
            BUNNY,
            RAINBOW,
            RAINBOWVR,
            ANGEL,
            POSTCARDFACE,
            THUMBSUP,
            PICKLEROBOY);


}

