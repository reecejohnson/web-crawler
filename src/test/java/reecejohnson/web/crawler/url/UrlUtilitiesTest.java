package reecejohnson.web.crawler.url;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import reecejohnson.web.crawler.TestAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UrlUtilitiesTest {

    private UrlUtilities urlUtilities;
    private TestAppender testAppender;

    @BeforeEach
    void setUp() {
        urlUtilities = new UrlUtilities();
        Logger logger = (Logger) LoggerFactory.getLogger(UrlUtilities.class);
        testAppender = new TestAppender();
        testAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.INFO);
        logger.addAppender(testAppender);
        testAppender.start();
    }

    @Nested
    class CleanseUrl {
        @Test
        void shouldRemoveTrailingSlashFromUrl() {
            var urlWithTrailingSlash = "https://example.com/";

            String result = urlUtilities.cleanseUrl(urlWithTrailingSlash);

            assertEquals("https://example.com", result);
        }

        @Test
        void shouldRemoveInternalPageSectionLinks() {
            var urlWithInternalPageSectionLink = "https://example.com/#section-one";

            String result = urlUtilities.cleanseUrl(urlWithInternalPageSectionLink);

            assertEquals("https://example.com", result);
        }

        @Test
        void shouldRemoveInternalPageSectionLinksWhenNotDirectlyAfterSlash() {
            var urlWithInternalPageSectionLink = "https://example.com/12as#sdse";

            String result = urlUtilities.cleanseUrl(urlWithInternalPageSectionLink);

            assertEquals("https://example.com/12as", result);
        }

        @Test
        void shouldTransformUrlToAllLowerCase() {
            var urlWithTrailingSlash = "https://example.com/About-Us";

            String result = urlUtilities.cleanseUrl(urlWithTrailingSlash);

            assertEquals("https://example.com/about-us", result);
        }
    }

    @Nested
    class IsUrlValid {

        @DisplayName("Should return true for valid url")
        @ParameterizedTest(name = "Should return true for valid url {0}")
        @ValueSource(strings = {
                "https://www.example.com",
                "http://www.example.com",
                "https://example.com",
                "https://example.com/about-us",
                "https://example.com/about-us/team",
                "https://example.com/about-us?param=123",
                "http://localhost:9898"
        })
        void shouldReturnTrueForValidUrl(String validUrl) {
            boolean result = urlUtilities.isUrlValid(validUrl);

            assertTrue(result);
        }

        @DisplayName("Should return false for invalid url")
        @ParameterizedTest(name = "Should return false for invalid url {0}")
        @ValueSource(strings = {
                "www.example.com",
                "example.com",
                "ftp://myname@host.dom",
                "not a url",
                "£@///as23",
                "htp://example.com"
        })
        void shouldReturnTrueForInvalidUrl(String invalidUrl) {
            boolean result = urlUtilities.isUrlValid(invalidUrl);

            assertFalse(result);
        }
    }

    @DisplayName("Should return false for urls from different domains")
    @ParameterizedTest(name = "Should return false for {0} and {1}")
    @MethodSource("urlsFromDifferentDomains")
    void shouldReturnFalseForUrlsFromDifferentDomains(String firstUrl, String secondUrl) {
        boolean result = urlUtilities.areUrlsTheSameDomain(firstUrl, secondUrl);

        assertFalse(result);
    }

    @DisplayName("Should return true for urls from the same domain")
    @ParameterizedTest(name = "Should return true for {0} and {1}")
    @MethodSource("urlsFromTheSameDomain")
    void shouldReturnTrueForUrlsFromTheSameDomain(String firstUrl, String secondUrl) {
        boolean result = urlUtilities.areUrlsTheSameDomain(firstUrl, secondUrl);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenInvalidUrlCausesException() {
        var validUrl = "https://www.example.com";
        var invalidUrl = "@@@###!!!sasuad";

        boolean result = urlUtilities.areUrlsTheSameDomain(validUrl, invalidUrl);

        assertFalse(result);
    }

    static Stream<Arguments> urlsFromTheSameDomain() {
        return Stream.of(
                Arguments.of("https://www.example.com", "https://www.example.com/about-us"),
                Arguments.of("https://www.example.com", "https://example.com"),
                Arguments.of("https://www.example.com", "https://www.example.com/company/values"),
                Arguments.of("http://localhost:9898", "http://localhost:9897"),
                Arguments.of("http://localhost:9898", "http://localhost:9898/page-one")
        );
    }

    static Stream<Arguments> urlsFromDifferentDomains() {
        return Stream.of(
                Arguments.of("https://www.example.com", "https://www.facebook.com"),
                Arguments.of("https://www.example.com", "https://www.community.example.com"),
                Arguments.of("https://example.com", "https://community.example.com"),
                Arguments.of("https://example.com", "https://www.community.example.com")
        );
    }
}
