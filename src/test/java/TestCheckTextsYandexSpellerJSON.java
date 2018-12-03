import beans.YandexSpellerAnswer;
import core.YandexSpellerApi;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static core.YandexSpellerConstants.*;
import static core.YandexSpellerConstants.Cities.*;
import static core.YandexSpellerConstants.SimpleWord.*;
import static enums.YandexSpellerErrorCodes.*;
import static enums.YandexSpellerLanguages.*;
import static enums.YandexSpellerOptions.*;
import static enums.YandexSpellerSoapActions.CHECK_TEXTS;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

public class TestCheckTextsYandexSpellerJSON {

    // simple usage of RestAssured library: direct request call and response validations in test.
    @Test
    public void simpleSpellerApiCall() {
        RestAssured
                .given()
                .queryParam(PARAM_TEXT, MOTHER.wrongVer())
                .params(PARAM_LANG, EN.getLanguageCode(), PARAM_TEXT, BROTHER.corrVer())
                .accept(ContentType.JSON)
                .auth().none()
                .header(HEADER_NAME, HEADER_VALUE)
                .and()
                .log().everything()
                .when()
                .get(YANDEX_SPELLER_API_URI + CHECK_TEXTS.getMethod())
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body(Matchers.allOf(
                        Matchers.stringContainsInOrder(Arrays.asList(MOTHER.wrongVer(), MOTHER.corrVer())),
                        Matchers.containsString(String.format("\"%s\":%d", ANSWER_CODE , ERROR_UNKNOWN_WORD.getCode()))))
                .contentType(ContentType.JSON)
                .time(lessThan(20000L)); // Milliseconds
    }

    // different http methods calls
    @Test
    public void spellerApiCallsWithDifferentMethods() {
        //GET
        RestAssured
                .given()
                .param(PARAM_TEXT, Arrays.asList(BROTHER.wrongVer(), MOTHER.wrongVer()))
                .log()
                .everything()
                .get(YANDEX_SPELLER_API_URI + CHECK_TEXTS.getMethod())
                .prettyPeek();
        System.out.println(repeat("=", 100));

        //POST
        RestAssured
                .given()
                .param(PARAM_TEXT, Arrays.asList(BROTHER.wrongVer(),MOTHER.wrongVer()))
                .log()
                .everything()
                .post(YANDEX_SPELLER_API_URI + CHECK_TEXTS.getMethod())
                .prettyPeek();
        System.out.println(repeat("=", 100));

        //HEAD
        RestAssured
                .given()
                .param(PARAM_TEXT, Arrays.asList(BROTHER.wrongVer(),MOTHER.wrongVer()))
                .log()
                .everything()
                .head(YANDEX_SPELLER_API_URI + CHECK_TEXTS.getMethod())
                .prettyPeek();
        System.out.println(repeat("=", 100));

        //OPTIONS
        RestAssured
                .given()
                .param(PARAM_TEXT, Arrays.asList(BROTHER.wrongVer(),MOTHER.wrongVer()))
                .log()
                .everything()
                .options(YANDEX_SPELLER_API_URI + CHECK_TEXTS.getMethod())
                .prettyPeek();
        System.out.println(repeat("=", 100));

        //PUT
        RestAssured
                .given()
                .param(PARAM_TEXT, Arrays.asList(BROTHER.wrongVer(),MOTHER.wrongVer()))
                .log()
                .everything()
                .put(YANDEX_SPELLER_API_URI + CHECK_TEXTS.getMethod())
                .prettyPeek();
        System.out.println(repeat("=", 100));

        //PATCH
        RestAssured
                .given()
                .param(PARAM_TEXT, Arrays.asList(BROTHER.wrongVer(), MOTHER.wrongVer()))
                .log()
                .everything()
                .patch(YANDEX_SPELLER_API_URI + CHECK_TEXTS.getMethod())
                .prettyPeek();
        System.out.println(repeat("=", 100));

        //DELETE
        RestAssured
                .given()
                .param(PARAM_TEXT, Arrays.asList(BROTHER.wrongVer(), MOTHER.wrongVer()))
                .log()
                .everything()
                .delete(YANDEX_SPELLER_API_URI + CHECK_TEXTS.getMethod())
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .statusLine("HTTP/1.1 405 Method not allowed");
    }

    //code API response OK
    @Test
    public void checkSuccessResponse() {
        YandexSpellerApi.with()
                .text(MOTHER.wrongVer())
                .callApi(CHECK_TEXTS)
                .then()
                .specification(YandexSpellerApi.successResponse());
    }

    //code API response Bad request, invalid lang
    @Test
    public void illegalLanguage() {
        YandexSpellerApi.with()
                .language(WRONG_SS)
                .text(MOTHER.wrongVer())
                .callApi(CHECK_TEXTS)
                .then()
                .specification(YandexSpellerApi.badRequestResponse("Invalid parameter '"+ PARAM_LANG + "'"));
    }

    //fail: code API response Bad request, invalid options
    @Test
    public void illegalOptions() {
        YandexSpellerApi.with()
                .options(WRONG_OPTIONS.getCode())
                .text(MOTHER.corrVer())
                .callApi(CHECK_TEXTS)
                .then()
                .specification(YandexSpellerApi.badRequestResponse("Invalid parameter '" + PARAM_OPTIONS + "'"));
    }

    //fail: don't work capitalization
    @Test
    public void wrongCapital() {
        List<List<YandexSpellerAnswer>> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi.with()
                                .language(EN)
                                .text(Arrays.asList(MOSCOW.corrVer(), LONDON.corrVer().toLowerCase()))
                                .callApi(CHECK_TEXTS));

        assertThat("expected number of answers.", answers.size(), equalTo(2));
        assertThat("expected one answer is wrong.", answers.get(1).size(), equalTo(1));
        assertThat("expected " + LONDON.corrVer().toLowerCase() +" answers is wrong.",
                answers.get(1).get(1).code, equalTo(ERROR_CAPITALIZATION.getCode()));
    }

    // ignore URLs
    @Test
    public void optionsIgnoreUrlsAndDigits() {
        List<List<YandexSpellerAnswer>> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi.with()
                                .text(Arrays.asList(MOSCOW.corrVer(), URL))
                                .language(EN)
                                .options(String.valueOf(IGNORE_DIGITS.getIntCode() + IGNORE_URLS.getIntCode()))
                                .callApi(CHECK_TEXTS));

        assertThat("expected number of answers.", answers.size(), equalTo(2));
        assertThat("expected ignore " + URL + ".", answers.get(1).size(), equalTo(0));
    }

    //validate other words
    @Test
    public void validateSpellerAnswerAsAnObject() {
        List<List<YandexSpellerAnswer>> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi.with()
                                .text(Arrays.asList(MOTHER.corrVer(), MOTHER.wrongVer(), LONDON.wrongVer()))
                                .callApi(CHECK_TEXTS));

        assertThat("expected number of answers.", answers.size(), equalTo(3));

        //check second wrong word
        assertThat("expected error code 1.", answers.get(1).get(0).code, equalTo(ERROR_UNKNOWN_WORD.getCode()));
        assertThat("expected error word " + MOTHER.wrongVer()+ ".", answers.get(1).get(0).word, equalTo(MOTHER.wrongVer()));
        assertThat("expected correct answers on " + MOTHER.corrVer() + ".", answers.get(1).get(0).s.get(0), equalTo(MOTHER.corrVer()));

        //check third wrong word
        assertThat("expected error code 1.", answers.get(2).get(0).code, equalTo(ERROR_UNKNOWN_WORD.getCode()));
        assertThat("expected error word " + LONDON.wrongVer()+ ".", answers.get(2).get(0).word, equalTo(LONDON.wrongVer()));
        assertThat("expected correct answers on " + LONDON.corrVer().toLowerCase() + ".", answers.get(2).get(0).s.get(0), equalTo(LONDON.corrVer().toLowerCase()));
    }

    //return correct variants in API response
    @Test
    public void correctVariants() {
        List<List<YandexSpellerAnswer>> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi.with()
                                .text(Arrays.asList(FATHER.wrongVer()))
                                .callApi(CHECK_TEXTS));

        for (String answer : answers.get(0).get(0).s) {
            List<List<YandexSpellerAnswer>> answers2 =
                    YandexSpellerApi.getYandexSpellerAnswers(
                            YandexSpellerApi.with()
                                    .text(Arrays.asList(answer))
                                    .callApi(CHECK_TEXTS));

            assertThat(String.format("Answer %s on word %s incorrect", answer, MOTHER.wrongVer()), answers2.get(0).size(), equalTo(0));
        }
    }

    //return incorrect variants in API response
    @Test
    public void incorrectVariants() {
        List<List<YandexSpellerAnswer>> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi.with()
                                .text(Arrays.asList(MOTHER.wrongVer()))
                                .callApi(CHECK_TEXTS));

        for (String answer : answers.get(0).get(0).s) {
            List<List<YandexSpellerAnswer>> answers2 =
                    YandexSpellerApi.getYandexSpellerAnswers(
                            YandexSpellerApi.with()
                                    .text(Arrays.asList(answer))
                                    .callApi(CHECK_TEXTS));

            assertThat(String.format("Answer %s on word %s incorrect", answer, MOTHER.wrongVer()), answers2.get(0).size(), equalTo(0));
        }
    }
}
