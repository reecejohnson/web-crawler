package reecejohnson.web.crawler;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;
import org.mockserver.model.MediaType;
import org.slf4j.LoggerFactory;
import reecejohnson.web.crawler.crawler.Crawler;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WebCrawlerAppTest {

    private ClientAndServer clientServer;
    private TestAppender testAppender;

    private static final Integer PORT = 9898;

    @BeforeEach
    void setup() {
        clientServer = ClientAndServer.startClientAndServer(PORT);
        Logger logger = (Logger) LoggerFactory.getLogger(Crawler.class);
        testAppender = new TestAppender();
        testAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.INFO);
        logger.addAppender(testAppender);
        testAppender.start();
    }

    @AfterEach
    void cleanUp() {
        if (clientServer.isRunning()) {
            clientServer.stop();
        }
    }

    @Test
    void shouldPrintCorrectAmountOfLinks() throws IOException {
        mockHttpCallToGetWebpage("/", "pageOne.html");
        mockHttpCallToGetWebpage("/page-two", "pageTwo.html");
        mockHttpCallToGetWebpage("/page-three", "pageThree.html");
        mockHttpCallToGetWebpage("/page-four", "pageFour.html");
        mockHttpCallToGetWebpage("/page-five", "pageOne.html");
        mockHttpCallToGetWebpage("/page-six", "pageOne.html");
        String[] arguments = {"http://localhost:" + PORT};

        WebCrawlerApp.main(arguments);

        assertLogsContain("Successfully crawled http://localhost:9898 and found 3 links");
        assertLogsContain("Successfully crawled http://localhost:9898/page-two and found 3 links");
        assertLogsContain("Successfully crawled http://localhost:9898/page-three and found 3 links");
        assertLogsContain("Successfully crawled http://localhost:9898/page-four and found 4 links");
        assertLogsContain("Successfully crawled http://localhost:9898/page-five and found 3 links");
        assertLogsContain("Successfully crawled http://localhost:9898/page-six and found 3 links");
        assertLogsContain("Finished crawl: http://localhost:9898 has 6 crawlable pages");
    }

    private String getHtmlFIleAsString(String fileName) throws IOException {
        return new String(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(fileName)).readAllBytes());
    }

    private void mockHttpCallToGetWebpage(String urlPath, String htmlFileName) throws IOException {
        String htmlFile = getHtmlFIleAsString(htmlFileName);
        clientServer.when(new HttpRequest().withMethod("GET").withPath(urlPath))
                .respond(new HttpResponse().withStatusCode(HttpStatusCode.OK_200.code())
                        .withBody(htmlFile, MediaType.HTML_UTF_8));
    }

    private void assertLogsContain(final String logMessage) {
        assertTrue(testAppender.findLogsContaining(logMessage).size() > 0);
    }
}
