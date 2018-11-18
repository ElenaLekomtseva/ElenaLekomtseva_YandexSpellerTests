package enums;

public enum YandexSpellerLanguages {

    RU("ru"),
    UK("uk"),
    EN("en"),
    WRONG_SS("ss");

    private String languageCode;
    public String getLanguageCode(){return languageCode;}

    private YandexSpellerLanguages(String lang) {
        this.languageCode = lang;
    }
}
