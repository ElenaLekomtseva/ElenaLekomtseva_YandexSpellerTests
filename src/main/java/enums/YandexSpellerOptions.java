package enums;

public enum YandexSpellerOptions {

    DEFAULT("0"),
    IGNORE_DIGITS("2"),
    IGNORE_URLS("4"),
    FIND_REPEAT_WORDS("8"),
    IGNORE_CAPITALIZATION("512"),
    WRONG_OPTIONS("77777");

    private String code;

    private YandexSpellerOptions(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
