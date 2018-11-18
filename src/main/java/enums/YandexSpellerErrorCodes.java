package enums;

public enum YandexSpellerErrorCodes {

    ERROR_UNKNOWN_WORD(1),
    ERROR_REPEAT_WORD(2),
    ERROR_CAPITALIZATION(3),
    ERROR_TOO_MANY_ERRORS(4);

    private int code;

    public int getCode(){
        return code;
    }

    private YandexSpellerErrorCodes(int code) {
        this.code = code;
    }
}
