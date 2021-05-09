package reecejohnson.web.crawler.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reecejohnson.web.crawler.models.WebPage;
import reecejohnson.web.crawler.url.UrlUtilities;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Crawler {

    private final LinkScraper linkScraper;
    private final UrlUtilities urlUtilities;

    public WebPage crawl(final String url) throws IOException {
        log.info("Starting to crawl {}", url);

        List<String> links = linkScraper.scrape(url)
                .stream()
                .filter(urlUtilities::isUrlValid)
                .filter(filterOutLinksFromDifferentDomains(url))
                .collect(Collectors.toList());

        log.info("Successfully crawled {} and found {} links", url, links.size());

        return WebPage.builder().url(url).links(links).build();
    }

    private Predicate<String> filterOutLinksFromDifferentDomains(String url) {
        return link -> urlUtilities.areUrlsTheSameDomain(url, link);
    }
}
