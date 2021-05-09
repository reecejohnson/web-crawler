package reecejohnson.web.crawler.crawler;

import lombok.RequiredArgsConstructor;
import reecejohnson.web.crawler.models.WebPage;
import reecejohnson.web.crawler.url.UrlUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void shouldCrawlSuccessfully() throws IOException {
        var pageOneUrl = URL + "/page-one";
        var pageTwoUrl = URL + "/page-two";
        var pageThreeUrl = URL + "/page-three";
        when(linkScraper.scrape(URL)).thenReturn(List.of(pageOneUrl, pageTwoUrl, pageThreeUrl));
        when(urlUtilities.isUrlValid(anyString())).thenReturn(true);
        when(urlUtilities.areUrlsTheSameDomain(anyString(), anyString())).thenReturn(true);

        WebPage webPage = crawler.crawl(URL);

        assertEquals(3, webPage.getLinks().size());
        verify(urlUtilities, times(3)).isUrlValid(anyString());
        verify(urlUtilities).isUrlValid(pageOneUrl);
        verify(urlUtilities).isUrlValid(pageTwoUrl);
        verify(urlUtilities).isUrlValid(pageThreeUrl);
        verify(urlUtilities, times(3)).areUrlsTheSameDomain(anyString(), anyString());
        verify(urlUtilities).areUrlsTheSameDomain(URL, pageOneUrl);
        verify(urlUtilities).areUrlsTheSameDomain(URL, pageTwoUrl);
        verify(urlUtilities).areUrlsTheSameDomain(URL, pageThreeUrl);
    }

    @Test
    void shouldExcludeUrlFromLinksWhenInvalid() throws IOException {
        var validUrl = URL + "/page-one";
        var invalidUrl = URL + "@[[]'';z";

        when(linkScraper.scrape(URL)).thenReturn(List.of(validUrl, invalidUrl));
        when(urlUtilities.isUrlValid(validUrl)).thenReturn(true);
        when(urlUtilities.isUrlValid(invalidUrl)).thenReturn(false);
        when(urlUtilities.areUrlsTheSameDomain(anyString(), anyString())).thenReturn(true);

        WebPage webPage = crawler.crawl(URL);

        assertEquals(1, webPage.getLinks().size());
        assertEquals(validUrl, webPage.getLinks().get(0));
    }

    @Test
    void shouldExcludeUrlFromLinksFromADifferentDomain() throws IOException {
        var urlFromSameDomain = URL + "/page-one";
        var urlFromDifferentDomain = "https://www.twitter.com";

        when(linkScraper.scrape(URL)).thenReturn(List.of(urlFromSameDomain, urlFromDifferentDomain));
        when(urlUtilities.isUrlValid(anyString())).thenReturn(true);
        when(urlUtilities.areUrlsTheSameDomain(URL, urlFromSameDomain)).thenReturn(true);
        when(urlUtilities.areUrlsTheSameDomain(URL, urlFromDifferentDomain)).thenReturn(false);

        WebPage webPage = crawler.crawl(URL);

        assertEquals(1, webPage.getLinks().size());
        assertEquals(urlFromSameDomain, webPage.getLinks().get(0));
    }
}
