package reecejohnson.web.crawler.crawler;

import lombok.RequiredArgsConstructor;
import reecejohnson.web.crawler.models.Sitemap;
import reecejohnson.web.crawler.models.WebPage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CrawlerOrchestrator {

    private final Crawler crawler;

    private final List<WebPage> webPages = new ArrayList<>();
    private final List<String> linksToCrawl = new ArrayList<>();
    private final List<String> linksCrawled = new ArrayList<>();

    public Sitemap start(final String url) {
        beginCrawl(url);

        while (linksToCrawl.size() > 0) {
            String link = linksToCrawl.get(0);
            linksToCrawl.remove(0);
            if (linksCrawled.contains(link)) {
                continue;
            }
            beginCrawl(link);
        }

        return Sitemap.builder().webPages(webPages).build();
    }

    private void beginCrawl(String link) {
        linksCrawled.add(link);
        Optional<WebPage> webPageOptional = crawler.crawl(link);
        if (webPageOptional.isPresent()) {
            var webPage = webPageOptional.get();
            webPages.add(webPage);
            addLinksToCrawlList(webPage.getLinks());
        }
    }

    private void addLinksToCrawlList(List<String> links) {
        var linksToAdd = links.stream()
                .filter(link -> !linksCrawled.contains(link))
                .filter(link -> !linksToCrawl.contains(link))
                .collect(Collectors.toList());
        linksToCrawl.addAll(linksToAdd);
    }
}
