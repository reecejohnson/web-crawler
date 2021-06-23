package reecejohnson.web.crawler.crawler;

import lombok.extern.slf4j.Slf4j;
import reecejohnson.web.crawler.models.Sitemap;
import reecejohnson.web.crawler.models.WebPage;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CrawlerOrchestrator {

    private final Crawler crawler;
    private final List<WebPage> webPages;
    private final List<String> linksToCrawl;
    private final List<String> linksCrawled;

    public CrawlerOrchestrator(final Crawler crawler) {
        this.crawler = crawler;
        this.webPages = new ArrayList<>();
        this.linksToCrawl = instantiateThreadSafeList();
        this.linksCrawled = instantiateThreadSafeList();
    }

    public Sitemap start(final String url, final int numberOfThreads) {
        final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        beginCrawl(url);

        while (linksToCrawl.size() > 0) {
            List<Callable<Optional<WebPage>>> callables = buildAsyncWebCrawlersForLinksToCrawl();

            try {
                executorService.invokeAll(callables)
                        .stream()
                        .map(handleWebPageFutureResponse())
                        .forEach(addWebPageAndLinksToSitemap());
            } catch (InterruptedException interruptedException) {
               log.error("Crawling stopped due to interrupted exception, returning partial sitemap.");
            }
        }

        return Sitemap.builder().webPages(webPages).build();
    }

    private void beginCrawl(final String link) {
        linksCrawled.add(link);
        Optional<WebPage> webPageOptional = crawler.crawl(link);
        if (webPageOptional.isPresent()) {
            var webPage = webPageOptional.get();
            webPages.add(webPage);
            addLinksToCrawlList(webPage.getLinks());
        }
    }

    private List<Callable<Optional<WebPage>>> buildAsyncWebCrawlersForLinksToCrawl() {
        List<Callable<Optional<WebPage>>> callables = new ArrayList<>();
        linksToCrawl.forEach(link -> {
            callables.add(buildAsyncWebCrawlerForLink(link));
        });
        return callables;
    }

    private Callable<Optional<WebPage>> buildAsyncWebCrawlerForLink(final String link) {
        return () -> {
            linksCrawled.add(link);
            linksToCrawl.remove(0);
            return crawler.crawl(link);
        };
    }

    private Function<Future<Optional<WebPage>>, Optional<WebPage>> handleWebPageFutureResponse() {
        return future -> {
            try {
                return future.get();
            } catch (Exception exception) {
                log.error("Error handling future response: {}", exception.getMessage());
                return Optional.empty();
            }
        };
    }

    private Consumer<Optional<WebPage>> addWebPageAndLinksToSitemap() {
        return webPageOptional -> {
            if (webPageOptional.isPresent()) {
                var webPage = webPageOptional.get();
                webPages.add(webPage);
                addLinksToCrawlList(webPage.getLinks());
            }
        };
    }

    private void addLinksToCrawlList(List<String> links) {
        var linksToAdd = links.stream()
                .filter(link -> !linksCrawled.contains(link))
                .filter(link -> !linksToCrawl.contains(link))
                .collect(Collectors.toList());
        linksToCrawl.addAll(linksToAdd);
    }

    private List<String> instantiateThreadSafeList() {
        return Collections.synchronizedList(new ArrayList<>());
    }

}
