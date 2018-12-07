import beans.YandexSpellerAnswer;
import core.YandexSpellerApi;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
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
import static io.restassured.http.Method.*;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TestCheckTextsYandexSpellerJSON {

    // different http methods calls
    @Test
    public void spellerApiCallsWithDifferentMethods() {
        //GET
        YandexSpellerApi.with()
                .text(Arrays.asList(BROTHER.wrongVer(), MOTHER.wrongVer()))
                .callApi(CHECK_TEXTS)
                .then()
                .specification(YandexSpellerApi.checkResponse(HttpStatus.SC_OK, ContentType.JSON));
        System.out.println(repeat("=", 100));

        //POST
        YandexSpellerApi.with()
                .text(Arrays.asList(BROTHER.wrongVer(), MOTHER.wrongVer()))
                .restMethod(POST)
                .callApi(CHECK_TEXTS)
                .then()
                .specification(YandexSpellerApi.checkResponse(HttpStatus.SC_OK, ContentType.JSON));
        System.out.println(repeat("=", 100));

        //HEAD
        YandexSpellerApi.with()
                .text(Arrays.asList(BROTHER.wrongVer(), MOTHER.wrongVer()))
                .restMethod(HEAD)
                .callApi(CHECK_TEXTS)
                .then()
                .specification(YandexSpellerApi.checkResponse(HttpStatus.SC_OK, ContentType.JSON));
        System.out.println(repeat("=", 100));

        //OPTIONS
        YandexSpellerApi.with()
                .text(Arrays.asList(BROTHER.wrongVer(), MOTHER.wrongVer()))
                .restMethod(OPTIONS)
                .callApi(CHECK_TEXTS)
                .then()
                .specification(YandexSpellerApi.checkResponse(HttpStatus.SC_OK, ""));
        System.out.println(repeat("=", 100));

        //PUT
        YandexSpellerApi.with()
                .text(Arrays.asList(BROTHER.wrongVer(), MOTHER.wrongVer()))
                .restMethod(PUT)
                .callApi(CHECK_TEXTS)
                .then()
                .specification(YandexSpellerApi.checkResponse(HttpStatus.SC_METHOD_NOT_ALLOWED, ""));
        System.out.println(repeat("=", 100));

        //PATCH
        YandexSpellerApi.with()
                .text(Arrays.asList(BROTHER.wrongVer(), MOTHER.wrongVer()))
                .restMethod(PATCH)
                .callApi(CHECK_TEXTS)
                .then()
                .specification(YandexSpellerApi.checkResponse(HttpStatus.SC_METHOD_NOT_ALLOWED, ""));
        System.out.println(repeat("=", 100));

        //DELETE
        YandexSpellerApi.with()
                .text(Arrays.asList(BROTHER.wrongVer(), MOTHER.wrongVer()))
                .restMethod(DELETE)
                .callApi(CHECK_TEXTS)
                .then()
                .specification(YandexSpellerApi.checkResponse(HttpStatus.SC_METHOD_NOT_ALLOWED, ""));
    }

    //code API response OK
    @Test
    public void checkSuccessResponse() {
        YandexSpellerApi.with()
                .text(MOTHER.wrongVer())
                .callApi(CHECK_TEXTS)
                .then()
                .specification(YandexSpellerApi.checkResponse(HttpStatus.SC_OK, ContentType.JSON));
    }

    //code API response Bad request, invalid lang
    @Test
    public void illegalLanguage() {
        YandexSpellerApi.with()
                .language(WRONG_SS)
                .text(MOTHER.wrongVer())
                .callApi(CHECK_TEXTS)
                .then()
                .specification(YandexSpellerApi.checkResponse(HttpStatus.SC_BAD_REQUEST,"Invalid parameter '"+ PARAM_LANG + "'"));
    }

    //fail: code API response Bad request, invalid options
    @Test
    public void illegalOptions() {
        YandexSpellerApi.with()
                .options(WRONG_OPTIONS)
                .text(MOTHER.corrVer())
                .callApi(CHECK_TEXTS)
                .then()
                .specification(YandexSpellerApi.checkResponse(HttpStatus.SC_BAD_REQUEST, "Invalid parameter '" + PARAM_OPTIONS + "'"));
    }

    //fail: don't work capitalization
    @Test
    public void wrongCapital() {
        String londonLowercase = LONDON.corrVer().toLowerCase();

        List<List<YandexSpellerAnswer>> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi.with()
                                .language(EN)
                                .text(Arrays.asList(MOSCOW.corrVer(), londonLowercase))
                                .callApi(CHECK_TEXTS));

        assertThat("expected number of answers.", answers.size(), equalTo(2));
        assertThat("expected one answer.", answers.get(1).size(), equalTo(1));
        assertThat("expected " + londonLowercase + " answers is wrong.", answers.get(1).get(1).word, equalTo(londonLowercase));
    }

    // ignore URLs
    @Test
    public void optionsIgnoreUrlsAndDigits() {
        List<List<YandexSpellerAnswer>> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi.with()
                                .text(Arrays.asList(URL))
                                .language(EN)
                                .options(IGNORE_DIGITS, IGNORE_URLS)
                                .callApi(CHECK_TEXTS));

        assertThat("expected number of answers.", answers.size(), equalTo(1));
        assertThat("expected ignore " + URL + ".", answers.get(0).size(), equalTo(0));
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
