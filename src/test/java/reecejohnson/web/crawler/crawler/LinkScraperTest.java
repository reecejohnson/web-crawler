package reecejohnson.web.crawler.crawler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;
import org.mockserver.model.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinkScraperTest {

    private LinkScraper linkScraper;
    private ClientAndServer clientServer;

    private static final Integer PORT = 9898;
    private static final String URL = "http://localhost:" + PORT.toString();

    @BeforeEach
    void setup() {
        linkScraper = new LinkScraper();
        clientServer = ClientAndServer.startClientAndServer(PORT);
    }

    @AfterEach
    void cleanUp() {
        if (clientServer.isRunning()) {
            clientServer.stop();
        }
    }

    @Test
    void shouldFindCorrectAmountOfLinks() throws IOException {
        String validHtmlPage = new String(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("pageOne.html")).readAllBytes());
        mockHttpCallToGetWebpage(validHtmlPage);

        List<String> links = linkScraper.scrape(URL);

        assertEquals(links.size(), 5);
    }

    @Test
    void shouldIgnoreLinkTagsWithNoHrefAttribute() throws IOException {
        mockHttpCallToGetWebpage(
                "<a>No href</a>" +
                "<a href=\"www.link.com\">Href</a");

        List<String> links = linkScraper.scrape(URL);

        assertEquals(links.size(), 1);
    }

    @Test
    void shouldReturnEmptyListWhenInvalidHtmlWithNoLinks() throws IOException {
        String invalidHtml = "<p><p><p><a>></a";
        mockHttpCallToGetWebpage(invalidHtml);

        List<String> links = linkScraper.scrape(URL);

        assertEquals(links.size(), 0);
    }

    private void mockHttpCallToGetWebpage(String htmlResponse) {
        clientServer.when(new HttpRequest().withMethod("GET"))
                .respond(new HttpResponse().withStatusCode(HttpStatusCode.OK_200.code())
                        .withBody(htmlResponse, MediaType.HTML_UTF_8));
    }
}
