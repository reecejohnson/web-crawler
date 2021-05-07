package reecejohnson.web.crawler.crawler;

import lombok.RequiredArgsConstructor;
import reecejohnson.web.crawler.models.WebPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class CrawlerTest {

    private Crawler crawler;
    private static final String URL = "https://site.com";

    @Mock
    private LinkScraper linkScraper;

    @BeforeEach
    void setup() {
        crawler = new Crawler(linkScraper);
    }

    @Test
    void shouldCrawlSuccessfully() throws IOException {
        when(linkScraper.scrape(URL)).thenReturn(List.of(URL + "/page-one", URL + "/page-two", URL + "/page-three"));

        WebPage webPage = crawler.crawl(URL);

        assertEquals(webPage.getLinks().size(), 3);
    }

}
