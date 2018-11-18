package enums;

public enum YandexSpellerSoapActions {

    CHECK_TEXT("checkText", "CheckTextRequest"),
    CHECK_TEXTS("checkTexts", "CheckTextsRequest");

    private String method;
    private String reqName;

    private YandexSpellerSoapActions(String action, String reqName) {
        this.method = action;
        this.reqName = reqName;
    }

    public String getMethod() {
        return this.method;
    }

    public String getReqName() {
        return this.reqName;
    }
}
