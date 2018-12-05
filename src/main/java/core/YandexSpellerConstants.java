package core;

/**
 * Created by yulia_atlasova@epam.com.
 * Constants of YandexSpeller
 */
public class YandexSpellerConstants {

    //useful constants for API under test
    public static final String YANDEX_SPELLER_API_URI = "https://speller.yandex.net/services/spellservice.json/";
    public static final String PARAM_TEXT = "text";
    public static final String PARAM_OPTIONS = "options";
    public static final String PARAM_LANG = "lang";
    public static final String ANSWER_CODE = "code";
    public static final String HEADER_NAME = "custom header2";
    public static final String HEADER_VALUE = "header1.value";
    public static final String WRONG_WORD_UK = "питаня";
    public static final String WORD_WITH_LEADING_DIGITS = "11" + SimpleWord.BROTHER.corrVer;
    public static final String QUOTES = "\"";
    public static final String URL = "https://techeer.yandex.ru";

    public enum SimpleWord{
        MOTHER("mother", "mottherr"),
        FATHER("father", "fathher"),
        BROTHER("brother", "bbrother");

        private String corrVer;
        private String wrongVer;

        public String corrVer(){return corrVer;}
        public String wrongVer(){return wrongVer;}

        private SimpleWord(String corrVer, String wrongVer){
            this.corrVer = corrVer;
            this.wrongVer = wrongVer;

        }
    }

    public enum Cities {

        MOSCOW("Moscow", "moscow"),
        RIGA("Riga", "Rigga"),
        LONDON("London", "londonn");

        private String corrVer;
        private String wrongVer;

        public String corrVer(){return corrVer;}
        public String wrongVer(){return wrongVer;}

        private Cities(String corrVer, String wrongVer){
            this.corrVer = corrVer;
            this.wrongVer = wrongVer;

        }
    }
}
