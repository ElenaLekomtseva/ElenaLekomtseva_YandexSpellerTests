package core;

import beans.YandexSpellerAnswer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.RequestSenderKinds;
import enums.YandexSpellerLanguages;
import enums.YandexSpellerOptions;
import enums.YandexSpellerSoapActions;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static core.YandexSpellerConstants.*;
import static org.hamcrest.Matchers.lessThan;

/**
 * Created by yulia_atlasova@epam.com
 * Describes Yandex Speller REST API
 */
public class YandexSpellerApi {

    //builder pattern
    private YandexSpellerApi() {
    }

    private HashMap<String, Object> params = new HashMap<String, Object>();

    public static class ApiBuilder {
        YandexSpellerApi spellerApi;

        private ApiBuilder(YandexSpellerApi gcApi) {
            spellerApi = gcApi;
        }

        public ApiBuilder text(String text) {
            spellerApi.params.put(PARAM_TEXT, text);
            return this;
        }

        public ApiBuilder text(List<String> texts) {
            spellerApi.params.put(PARAM_TEXT, texts);
            return this;
        }

        public ApiBuilder options(YandexSpellerOptions... options) {
            int totalOption = 0;
            for (YandexSpellerOptions option : options) {
                totalOption += option.getCode();
            }
            spellerApi.params.put(PARAM_OPTIONS, totalOption);
            return this;
        }

        public ApiBuilder language(YandexSpellerLanguages language) {
            spellerApi.params.put(PARAM_LANG, language.getLanguageCode());
            return this;
        }

        public Response callApi(YandexSpellerSoapActions action) {
            return callApi(action, RequestSenderKinds.GET);
        }

        public Response callApi(YandexSpellerSoapActions action, RequestSenderKinds typeRequest) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String uri = YANDEX_SPELLER_API_URI + action.getMethod();

            RequestSpecification request = RestAssured.with()
                    .queryParams(spellerApi.params)
                    .log().all();

            Response response;
            switch (typeRequest) {
                case POST:
                    response = request.post(uri);
                    break;
                case HEAD:
                    response = request.head(uri);
                    break;
                case OPTIONS:
                    response = request.options(uri);
                    break;
                case PUT:
                    response = request.put(uri);
                    break;
                case PATCH:
                    response = request.patch(uri);
                    break;
                case DELETE:
                    response = request.delete(uri);
                    break;
                default:
                    response = request.get(uri);
                    break;
            }
            return response.prettyPeek();
        }
    }

    public static ApiBuilder with() {
        YandexSpellerApi api = new YandexSpellerApi();
        return new ApiBuilder(api);
    }

    //get ready Speller answers list form api response
    public static List<YandexSpellerAnswer> getYandexSpellerAnswer(Response response) {
        return new Gson().fromJson(response.asString().trim(), new TypeToken<List<YandexSpellerAnswer>>() {
        }.getType());
    }

    //get ready Speller answers list form api response
    public static List<List<YandexSpellerAnswer>> getYandexSpellerAnswers(Response response) {
        return new Gson().fromJson(response.asString().trim(), new TypeToken<List<List<YandexSpellerAnswer>>>() {
        }.getType());
    }

    //set base request and response specifications tu use in tests
    public static ResponseSpecification successResponse() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectHeader("Connection", "keep-alive")
                .expectResponseTime(lessThan(20000L))
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    //set base request and response specifications tu use in tests
    public static ResponseSpecification checkResponse(int statusCode, String expectBody) {
        return new ResponseSpecBuilder()
                .expectHeader("Connection", "keep-alive")
                .expectResponseTime(lessThan(20000L))
                .expectStatusCode(statusCode)
                .expectBody(Matchers.containsString(expectBody))
                .build();
    }

    //set base request and response specifications tu use in tests
    public static ResponseSpecification checkResponse(int statusCode, ContentType contentType) {
        return new ResponseSpecBuilder()
                .expectContentType(contentType)
                .expectHeader("Connection", "keep-alive")
                .expectResponseTime(lessThan(20000L))
                .expectStatusCode(statusCode)
                .build();
    }

    public static RequestSpecification baseRequestConfiguration(YandexSpellerSoapActions action) {
        return new RequestSpecBuilder()
                .setAccept(ContentType.XML)
                .setRelaxedHTTPSValidation()
                .addHeader(HEADER_NAME, HEADER_VALUE)
                .addQueryParam("requestID", new Random().nextLong())
                .setBaseUri(YANDEX_SPELLER_API_URI + action.getMethod())
                .build();
    }
}
