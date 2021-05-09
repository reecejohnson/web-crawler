package reecejohnson.web.crawler.crawler;

import reecejohnson.web.crawler.models.Sitemap;
import reecejohnson.web.crawler.models.WebPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class CrawlerOrchestratorTest {

    private CrawlerOrchestrator crawlerOrchestrator;

    private final String baseUrl = "https://url.com";

    @Mock
    private Crawler crawler;

    @BeforeEach
    void setup() {
        crawlerOrchestrator = new CrawlerOrchestrator(crawler);
    }

    @Test
    void shouldReturnSitemapWithCorrectLinks() {
        String childAUrl = baseUrl + "/child-a";
        String childBUrl = baseUrl + "/child-b";
        String childCUrl = baseUrl + "/child-c";

        List<String> baseUrlLinks = List.of(childAUrl);
        when(crawler.crawl(baseUrl)).thenReturn(buildWebPageWithLinks(baseUrl, baseUrlLinks));

        List<String> childALinks = List.of(childBUrl);
        when(crawler.crawl(childAUrl)).thenReturn(buildWebPageWithLinks(childAUrl, childALinks));

        List<String> childBLinks = List.of(childCUrl);
        when(crawler.crawl(childBUrl)).thenReturn(buildWebPageWithLinks(childBUrl, childBLinks));

        List<String> childCLinks = List.of();
        when(crawler.crawl(childCUrl)).thenReturn(buildWebPageWithLinks(childCUrl, childCLinks));

        Sitemap sitemap = crawlerOrchestrator.start(baseUrl);

        assertEquals(4, sitemap.getWebPages().size());
        assertEquals(baseUrl, sitemap.getWebPages().get(0).getUrl());
        assertEquals(baseUrlLinks, sitemap.getWebPages().get(0).getLinks());
        assertEquals(childAUrl, sitemap.getWebPages().get(1).getUrl());
        assertEquals(childALinks, sitemap.getWebPages().get(1).getLinks());
        assertEquals(childBUrl, sitemap.getWebPages().get(2).getUrl());
        assertEquals(childBLinks, sitemap.getWebPages().get(2).getLinks());
        assertEquals(childCUrl, sitemap.getWebPages().get(3).getUrl());
        assertEquals(childCLinks, sitemap.getWebPages().get(3).getLinks());
    }

    @Test
    void shouldOnlyCrawlTheSameLinkOnceWithOneResultantWebPageWhenOnMultiplePages() {
        String childAUrl = baseUrl + "/child-a";
        String childBUrl = baseUrl + "/child-b";

        List<String> baseUrlLinks = List.of(baseUrl, childAUrl);
        when(crawler.crawl(baseUrl)).thenReturn(buildWebPageWithLinks(baseUrl, baseUrlLinks));

        List<String> childALinks = List.of(baseUrl, childAUrl, childBUrl);
        when(crawler.crawl(childAUrl)).thenReturn(buildWebPageWithLinks(childAUrl, childALinks));

        List<String> childBLinks = List.of(baseUrl, childAUrl);
        when(crawler.crawl(childBUrl)).thenReturn(buildWebPageWithLinks(childBUrl, childBLinks));

        Sitemap sitemap = crawlerOrchestrator.start(baseUrl);

        assertEquals(3, sitemap.getWebPages().size());
        verify(crawler, times(1)).crawl(baseUrl);
        verify(crawler, times(1)).crawl(childAUrl);
    }

    @Test
    void shouldIgnoreCrawlerResponseWhenEmpty() {
        when(crawler.crawl(baseUrl)).thenReturn(Optional.empty());
        Sitemap sitemap = crawlerOrchestrator.start(baseUrl);
        assertEquals(0, sitemap.getWebPages().size());
    }

    private Optional<WebPage> buildWebPageWithLinks(String url, List<String> links) {
        return Optional.of(WebPage.builder().url(url).links(links).build());
    }
}
