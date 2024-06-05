package Location;

import accuweather.AccuweatherAbstractTest;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import location.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;

public class LocationTest extends AccuweatherAbstractTest {

    @Test
    void testGetLocationsWithNewAssertions() {
        Map<String, String> mapQuery = new HashMap<>();

        mapQuery.put("apikey", getApiKey());
        mapQuery.put("q", "Moscow");

        List<Location> list = given().queryParams(mapQuery)
                .when().get(getBaseUrl() + "/locations/v1/cities/autocomplete")
                .then().statusCode(200).time(lessThan(3000L))
                .extract().body().jsonPath().getList(".", Location.class);

        Assertions.assertAll(() -> Assertions.assertEquals(10,
                        list.size()),
                () -> Assertions.assertEquals("Moscow",
                        list.get(5).getLocalizedName()),
                () -> Assertions.assertEquals("City",
                        list.get(0).getType()),
                () -> Assertions.assertEquals("294021",
                        list.get(0).getKey()),
                () -> Assertions.assertEquals("RU",
                        list.get(0).getCountry().getId()),
                () -> Assertions.assertEquals("Russia",
                        list.get(0).getCountry().getLocalizedName()),
                () -> Assertions.assertEquals("MOW",
                        list.get(0).getAdministrativeArea().getId()),
                () -> Assertions.assertEquals("Moscow", list.get(0).
                        getAdministrativeArea().getLocalizedName()));
    }

    @Test
    void testGetResponseLocations() {

        Response response = given().queryParams("apikey", getApiKey(), "q", "Moscow")
                .when().request(Method.GET, getBaseUrl() + "/locations/v1/cities/autocomplete");

        int statusCode = response.getStatusCode();
        List locationList = response.getHeaders().asList();
        String header = response.getHeader("Content-Encoding");
        String contentType = response.getContentType();

        Assertions.assertEquals(200, statusCode);
        Assertions.assertEquals(25, locationList.size());
        Assertions.assertEquals("gzip", header);
        Assertions.assertEquals(ContentType.JSON.withCharset(StandardCharsets.UTF_8).
                toLowerCase(Locale.ROOT), contentType);

    }
}
