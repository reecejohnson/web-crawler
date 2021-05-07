package reecejohnson.web.crawler.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reecejohnson.web.crawler.models.WebPage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Crawler {

    private final LinkScraper linkScraper;

    public WebPage crawl(final String url) throws IOException {
        log.info("Starting to crawl {}", url);

        List<String> links = linkScraper.scrape(url);

        log.info("Successfully crawled {} and found {} links", url, links.size());

        return WebPage.builder().url(url).links(links).build();
    }
}
