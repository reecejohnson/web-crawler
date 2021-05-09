package reecejohnson.web.crawler.crawler;

import lombok.RequiredArgsConstructor;
import reecejohnson.web.crawler.models.UrlScrapeException;
import reecejohnson.web.crawler.models.WebPage;
import reecejohnson.web.crawler.url.UrlUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpConnectTimeoutException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class CrawlerTest {

    private Crawler crawler;
    private static final String URL = "https://site.com";

    @Mock
    private LinkScraper linkScraper;
    @Mock
    private UrlUtilities urlUtilities;

    @BeforeEach
    void setup() {
        crawler = new Crawler(linkScraper, urlUtilities);
    }

    @Test
    void shouldCrawlSuccessfully() throws UrlScrapeException {
        var pageOneUrl = URL + "/page-one";
        var pageTwoUrl = URL + "/page-two";
        var pageThreeUrl = URL + "/page-three";
        when(urlUtilities.cleanseUrl(URL)).thenReturn(URL);
        when(linkScraper.scrape(URL)).thenReturn(List.of(pageOneUrl, pageTwoUrl, pageThreeUrl));
        when(urlUtilities.isUrlValid(anyString())).thenReturn(true);
        when(urlUtilities.areUrlsTheSameDomain(anyString(), anyString())).thenReturn(true);
        when(urlUtilities.cleanseUrl(pageOneUrl)).thenReturn(pageOneUrl);
        when(urlUtilities.cleanseUrl(pageTwoUrl)).thenReturn(pageTwoUrl);
        when(urlUtilities.cleanseUrl(pageThreeUrl)).thenReturn(pageThreeUrl);

        var webPage = crawler.crawl(URL).get();

        assertEquals(3, webPage.getLinks().size());
        verify(urlUtilities, times(3)).isUrlValid(anyString());
        verify(urlUtilities).isUrlValid(pageOneUrl);
        verify(urlUtilities).isUrlValid(pageTwoUrl);
        verify(urlUtilities).isUrlValid(pageThreeUrl);
        verify(urlUtilities, times(3)).areUrlsTheSameDomain(anyString(), anyString());
        verify(urlUtilities).areUrlsTheSameDomain(URL, pageOneUrl);
        verify(urlUtilities).areUrlsTheSameDomain(URL, pageTwoUrl);
        verify(urlUtilities).areUrlsTheSameDomain(URL, pageThreeUrl);
        verify(urlUtilities, times(4)).cleanseUrl(any());
        verify(urlUtilities).cleanseUrl(URL);
        verify(urlUtilities).cleanseUrl(pageOneUrl);
        verify(urlUtilities).cleanseUrl(pageTwoUrl);
        verify(urlUtilities).cleanseUrl(pageThreeUrl);
    }

    @Test
    void shouldExcludeUrlFromLinksWhenInvalid() throws UrlScrapeException {
        var validUrl = URL + "/page-one";
        var invalidUrl = URL + "@[[]'';z";

        when(urlUtilities.cleanseUrl(URL)).thenReturn(URL);
        when(linkScraper.scrape(URL)).thenReturn(List.of(validUrl, invalidUrl));
        when(urlUtilities.isUrlValid(validUrl)).thenReturn(true);
        when(urlUtilities.isUrlValid(invalidUrl)).thenReturn(false);
        when(urlUtilities.areUrlsTheSameDomain(anyString(), anyString())).thenReturn(true);
        when(urlUtilities.cleanseUrl(validUrl)).thenReturn(validUrl);

        var webPage = crawler.crawl(URL).get();

        assertEquals(1, webPage.getLinks().size());
        assertEquals(validUrl, webPage.getLinks().get(0));
    }

    @Test
    void shouldExcludeUrlFromLinksFromADifferentDomain() throws UrlScrapeException {
        var urlFromSameDomain = URL + "/page-one";
        var urlFromDifferentDomain = "https://www.twitter.com";

        when(urlUtilities.cleanseUrl(URL)).thenReturn(URL);
        when(linkScraper.scrape(URL)).thenReturn(List.of(urlFromSameDomain, urlFromDifferentDomain));
        when(urlUtilities.isUrlValid(anyString())).thenReturn(true);
        when(urlUtilities.areUrlsTheSameDomain(URL, urlFromSameDomain)).thenReturn(true);
        when(urlUtilities.areUrlsTheSameDomain(URL, urlFromDifferentDomain)).thenReturn(false);
        when(urlUtilities.cleanseUrl(urlFromSameDomain)).thenReturn(urlFromSameDomain);

        var webPage = crawler.crawl(URL).get();

        assertEquals(1, webPage.getLinks().size());
        assertEquals(urlFromSameDomain, webPage.getLinks().get(0));
    }

    @Test
    void shouldReturnEmptyOptionalWhenErrorScrapingUrl() throws UrlScrapeException {
        when(urlUtilities.cleanseUrl(URL)).thenReturn(URL);
        when(linkScraper.scrape(URL))
                .thenThrow(UrlScrapeException.create(URL, new HttpConnectTimeoutException("error")));

        var webPageOptional = crawler.crawl(URL);

        assertTrue(webPageOptional.isEmpty());
    }

    @Test
    void shouldRemoveDuplicateLinks() throws UrlScrapeException {
        var duplicatedLink = URL + "/page-one";

        when(urlUtilities.cleanseUrl(URL)).thenReturn(URL);
        when(linkScraper.scrape(URL)).thenReturn(List.of(duplicatedLink, duplicatedLink));
        when(urlUtilities.isUrlValid(anyString())).thenReturn(true);
        when(urlUtilities.cleanseUrl(duplicatedLink)).thenReturn(duplicatedLink);
        when(urlUtilities.areUrlsTheSameDomain(anyString(), anyString())).thenReturn(true);

        var webPage = crawler.crawl(URL).get();

        assertEquals(1, webPage.getLinks().size());
        assertEquals(duplicatedLink, webPage.getLinks().get(0));
    }
}
