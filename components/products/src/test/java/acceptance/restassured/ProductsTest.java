package acceptance.restassured;

import org.junit.*;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.verify.VerificationTimes;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class ProductsTest {
    private MockServerClient mockServer;

    @Before
    public void setup() {
        mockServer = startClientAndServer(6789);
    }

    @After
    public void teardown() {
        mockServer.stop();
    }

    @Test
    public void testGetProducts() {
        mockServer
            .when(request().withMethod("GET").withPath("/external-product"))
            .respond(response().withStatusCode(200).withBody("Some External Product"));

        get("http://127.0.0.1:8080/products")
            .then().assertThat().body(matchesJsonSchemaInClasspath("products-schema.json"))
            .and().assertThat().body("size()", equalTo(3))
            .and().assertThat().body("get(0).name", equalTo("Super Glue"))
            .and().assertThat().body("get(1).name", equalTo("Kool-Aide"))
            .and().assertThat().body("get(2).name", equalTo("Some External Product"));

        mockServer.verify(request().withMethod("GET")
            .withPath("/external-product"),
            VerificationTimes.exactly(1));
    }

    @Test
    public void testGetProducts_ExternalServerNotFound() {
        mockServer
            .when(request().withMethod("GET").withPath("/external-product"))
            .respond(response().withStatusCode(404));

        get("http://127.0.0.1:8080/products")
            .then().assertThat().body(matchesJsonSchemaInClasspath("products-schema.json"))
            .and().assertThat().body("size()", equalTo(2))
            .and().assertThat().body("get(0).name", equalTo("Super Glue"))
            .and().assertThat().body("get(1).name", equalTo("Kool-Aide"));
    }
}
