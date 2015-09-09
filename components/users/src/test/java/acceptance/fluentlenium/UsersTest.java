package acceptance.fluentlenium;

import org.fluentlenium.adapter.FluentTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsersTest extends FluentTest {
    @Test
    public void testGetIndex() {
        goTo("http://127.0.0.1:8080/applications/core");
        assertThat(pageSource()).contains("Hello world!");
    }

    @Test
    public void testGetHealth() {
        goTo("http://127.0.0.1:8080/applications/core/health");
        assertThat(pageSource()).contains("OK: You are using application-test.yml!");
    }
}
