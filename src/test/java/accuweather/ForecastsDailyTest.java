package accuweather;


import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import location.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import weather.Weather;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

public class ForecastsDailyTest extends AccuweatherAbstractTest {

    @Test
    void testGetResponse1Day() {
        Weather weather = given().queryParam("apikey", getApiKey()).pathParam("locationKey", 50)
                .when().get(getBaseUrl() + "/forecasts/v1/daily/1day/{locationKey}")
                .then().statusCode(200).time(lessThan(2000L))
                .extract().response().body().as(Weather.class);
        Assertions.assertEquals(1, weather.getDailyForecasts().size());
        System.out.println(weather);
    }


    @Test
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