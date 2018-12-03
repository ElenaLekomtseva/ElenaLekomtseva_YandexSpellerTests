package core;

import enums.YandexSpellerLanguages;
import enums.YandexSpellerSoapActions;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;

import static core.YandexSpellerConstants.*;

/**
 * Created by yulia_atlasova@epam.com.
 * Describes Yandex Speller SOAP request.
 */
public class YandexSpellerSOAP {

    static RequestSpecification spellerSOAPreqSpec = new RequestSpecBuilder()
            .addHeader("Accept-Encoding", "gzip,deflate")
            .setContentType("text/xml;charset=UTF-8")
            .addHeader("Host", "speller.yandex.net")
            .setBaseUri("http://speller.yandex.net/services/spellservice")
            .build();

    //builder pattern
    private YandexSpellerSOAP(){}

    private HashMap<String, String> params = new HashMap<>();
    private YandexSpellerSoapActions action = YandexSpellerSoapActions.CHECK_TEXT;

    public static class SOAPBuilder {
        YandexSpellerSOAP soapReq;

        private SOAPBuilder(YandexSpellerSOAP soap) {
            this.soapReq = soap;
        }

        public SOAPBuilder action(YandexSpellerSoapActions action){
            soapReq.action = action;
            return this;
        }

        public SOAPBuilder text(String text) {
            soapReq.params.put(PARAM_TEXT, text);
            return this;
        }

        public SOAPBuilder options(String options) {
            soapReq.params.put(PARAM_OPTIONS, options);
            return this;
        }

        public SOAPBuilder language(YandexSpellerLanguages language) {
            soapReq.params.put(PARAM_LANG, language.getLanguageCode());
            return this;
        }

        public Response callSOAP() {
            String soapBody="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:spel=\"http://speller.yandex.net/services/spellservice\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <spel:" + soapReq.action.getReqName() + " lang=" + QUOTES + (soapReq.params.getOrDefault(PARAM_LANG, "en")) + QUOTES
                    +  " options=" + QUOTES + (soapReq.params.getOrDefault(PARAM_OPTIONS, "0"))+ QUOTES
                    + " format=\"\">\n" +
                    "         <spel:text>"+ (soapReq.params.getOrDefault(PARAM_TEXT, SimpleWord.BROTHER.wrongVer())) + "</spel:text>\n" +
                    "      </spel:"+ soapReq.action.getReqName() + ">\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";


            return RestAssured.with()
                    .spec(spellerSOAPreqSpec)
                    .header("SOAPAction", "http://speller.yandex.net/services/spellservice/" + soapReq.action.getMethod())
                    .body(soapBody)
                    .log().all().with()
                    .post().prettyPeek();
        }
    }


    public static SOAPBuilder with() {
        YandexSpellerSOAP soap = new YandexSpellerSOAP();
        return new SOAPBuilder(soap);
    }
}
