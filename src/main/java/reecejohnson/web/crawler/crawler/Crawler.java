package reecejohnson.web.crawler.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reecejohnson.web.crawler.models.UrlScrapeException;
import reecejohnson.web.crawler.models.WebPage;
import reecejohnson.web.crawler.url.UrlUtilities;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Crawler {

    private final LinkScraper linkScraper;
    private final UrlUtilities urlUtilities;

    public Optional<WebPage> crawl(final String url) {
        try {
            String cleanUrl = urlUtilities.cleanseUrl(url);
            log.info("Starting to crawl {}", cleanUrl);

            List<String> links = linkScraper.scrape(cleanUrl)
                    .stream()
                    .filter(urlUtilities::isUrlValid)
                    .filter(filterOutLinksFromDifferentDomains(cleanUrl))
                    .map(urlUtilities::cleanseUrl)
                    .distinct()
                    .collect(Collectors.toList());

            log.info("Successfully crawled {} and found {} links", cleanUrl, links.size());
            return Optional.of(WebPage.builder().url(cleanUrl).links(links).build());
        } catch (final UrlScrapeException urlScrapeException) {
            return Optional.empty();
        }
    }

    private Predicate<String> filterOutLinksFromDifferentDomains(final String url) {
        return link -> urlUtilities.areUrlsTheSameDomain(url, link);
    }
}
