package accuweather;


import io.qameta.allure.*;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import location.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import weather.Weather;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

@Epic("Тестирование проекта accuweather.com")
@Feature("Тестирование API Forecast")
public class ForecastsDailyTest extends AccuweatherAbstractTest {

    @Test
    @DisplayName("Тест ForecastsDailyTest - получение ответа 1 день")
    @Link("https://developer.accuweather.com/accuweather-forecast-api/apis")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Вызов метода получения погоды за 1 день")
    void testGetResponse1Day() {
        Weather weather = given().queryParam("apikey", getApiKey()).pathParam("locationKey", 50)
                .when().get(getBaseUrl() + "/forecasts/v1/daily/1day/{locationKey}")
                .then().statusCode(200).time(lessThan(2000L))
                .extract().response().body().as(Weather.class);
        Assertions.assertEquals(1, weather.getDailyForecasts().size());
        System.out.println(weather);
    }


    @Test
    @DisplayName("Тест ForecastsDailyTest - получение ответа 10 дней")
    @Link("https://developer.accuweather.com/accuweather-forecast-api/apis")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Вызов метода получения погоды за 10 дней")
    void testGetResponse10Days() {
        String code = given().queryParam("apikey", getApiKey()).pathParam("locationKey", 50)
                .when()
                .get(getBaseUrl() + "/forecasts/v1/daily/10day/{locationKey}")
                .then().statusCode(401).extract()
                .jsonPath()
                .getString("Code");

        String message = given().queryParam("apikey", getApiKey()).pathParam("locationKey", 50)
                .when()
                .get(getBaseUrl() + "/forecasts/v1/daily/10day/{locationKey}")
                .then().statusCode(401).extract()
                .jsonPath()
                .getString("Message");
        Assertions.assertAll(() -> Assertions.assertEquals("Unauthorized", code),
                () -> Assertions.assertEquals("Api Authorization failed", message));
    }

    @Test
    @DisplayName("Тест ForecastsDailyTest - получение ответа 15 дней")
    @Link("https://developer.accuweather.com/accuweather-forecast-api/apis")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Вызов метода получения погоды за 15 дней")
    void testGetResponse15Days() {

        String code = given().queryParam("apikey", getApiKey()).pathParam("locationKey", 50)
                .when()
                .get(getBaseUrl() + "/forecasts/v1/daily/15day/{locationKey}")
                .then().statusCode(401).extract()
                .jsonPath()
                .getString("Code");

        String message = given().queryParam("apikey", getApiKey()).pathParam("locationKey", 50)
                .when()
                .get(getBaseUrl() + "/forecasts/v1/daily/15day/{locationKey}")
                .then().statusCode(401).extract()
                .jsonPath()
                .getString("Message");
        Assertions.assertAll(() -> Assertions.assertEquals("Unauthorized", code),
                () -> Assertions.assertEquals("Api Authorization failed", message));

    }

    @Test
    @DisplayName("Тест ForecastsDailyTest - поиск по дате ответа с автозаполнением")
    @Link("https://developer.accuweather.com/accuweather-forecast-api/apis")
    @Severity(SeverityLevel.NORMAL)
    @Story("Поиск по дате ответа с автозаполнением")
    void testResponseDateAutocompleteSearch() {
        //вариант через assertThat() (но после первого бага проверка прекращается)
        given().queryParam("apikey", getApiKey()).queryParam("q", "Moscow")
                .when().request(Method.GET, getBaseUrl() + "/locations/v1/cities/autocomplete")
                .then().assertThat().statusCode(200).time(lessThan(2000L))
                .statusLine("HTTP/1.1 200 OK")
                .header("Content-Encoding", "gzip")
                .body("[0].LocalizedName", equalTo("Moscow"))
                .body("[0].Key", equalTo("294021"));
        //вариант через assertAll() (выводит все баги)
        JsonPath response = given().queryParam("apikey", getApiKey()).queryParam("q", "Moscow")
                .when().request(Method.GET, getBaseUrl() + "/locations/v1/cities/autocomplete")
                .body().jsonPath();
        Assertions.assertAll(() -> Assertions.assertEquals("Moscow", response.get("[0].LocalizedName")),
                () -> Assertions.assertEquals("294021", response.get("[0].Key")));


    }

    @Test
    @DisplayName("Тест ForecastsDailyTest - получение Locations")
    @Link("https://developer.accuweather.com/accuweather-forecast-api/apis")
    @Severity(SeverityLevel.NORMAL)
    @Story("Получение локации")
    void testGetLocations() {
        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("apikey", getApiKey());
        mapQuery.put("q", "London");
        List<Location> listLocations = given().queryParams(mapQuery)
                .when().get(getBaseUrl() + "/locations/v1/cities/autocomplete")
                .then().statusCode(200)
                .extract().body().jsonPath().getList(".", Location.class);
        Assertions.assertAll(() -> Assertions.assertEquals(10, listLocations.size()),
                () -> Assertions.assertEquals("London", listLocations.get(0).getLocalizedName()));

    }
}