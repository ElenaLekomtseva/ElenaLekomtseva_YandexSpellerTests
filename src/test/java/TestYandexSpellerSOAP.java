import core.YandexSpellerSOAP;
import enums.YandexSpellerLanguages;
import enums.YandexSpellerSoapActions;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;

import static core.YandexSpellerConstants.*;

/**
 * Created by yulia_atlasova@epam.com.
 * try to test SOAP via RestAssured
 */
public class TestYandexSpellerSOAP {

    @Test
    public void simpleCall(){
                YandexSpellerSOAP
                        .with()
                        .text(SimpleWord.BROTHER.wrongVer())
                        .callSOAP()
                        .then()
                        .body(Matchers.stringContainsInOrder
                                (Arrays.asList(SimpleWord.BROTHER.wrongVer(), SimpleWord.BROTHER.corrVer())));
    }

    @Test
    public void useRequestBuilderToChangeParams(){
        YandexSpellerSOAP.with()
                .language(YandexSpellerLanguages.EN)
                .text(SimpleWord.BROTHER.wrongVer())
                .options("6")
                .action(YandexSpellerSoapActions.CHECK_TEXTS)
                .callSOAP()
                .then()
                .body(Matchers.stringContainsInOrder
                        (Arrays.asList(SimpleWord.BROTHER.wrongVer(), SimpleWord.BROTHER.corrVer())));
    }
}
