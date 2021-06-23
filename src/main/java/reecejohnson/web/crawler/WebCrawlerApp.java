package reecejohnson.web.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reecejohnson.web.crawler.crawler.CrawlerOrchestrator;
import reecejohnson.web.crawler.html.HtmlOutputBuilder;
import reecejohnson.web.crawler.models.InvalidArgumentException;
import reecejohnson.web.crawler.models.Sitemap;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class WebCrawlerApp implements CommandLineRunner {

    private final CrawlerOrchestrator crawlerOrchestrator;
    private final HtmlOutputBuilder htmlOutputBuilder;

    public static void main(String[] args) {
        SpringApplication.run(WebCrawlerApp.class, args);
    }

    @Override
    public void run(String... args) throws InvalidArgumentException {
        String url = getUrlFromArguments(args);
        int numberOfThreads = getNumberOfThreadsFromArguments( args);

        final Sitemap sitemap = crawlerOrchestrator.start(url, numberOfThreads);

        String finishedCrawlMessage = String.format("Finished crawl: %s has %s crawlable pages", url, sitemap.getWebPages().size());
        log.info(finishedCrawlMessage);
        System.out.println(finishedCrawlMessage);

        String sitemapString = sitemap.toString();
        log.info(sitemapString);
        System.out.println(sitemapString);

        htmlOutputBuilder.buildOutputFile(sitemap);
    }

    private String getUrlFromArguments(final String... args) throws InvalidArgumentException {
        try {
            return args[0];
        } catch (Exception exception) {
            final String errorMessage = "Invalid argument. Argument #1: Url - must be a string.";
            throw InvalidArgumentException.create(errorMessage);
        }
    }

    private int getNumberOfThreadsFromArguments(final String... args) throws InvalidArgumentException {
        try {
             return Integer.parseInt(args[1]);
        } catch (Exception exception) {
            final String errorMessage = "Invalid argument. Argument #2: Number of Threads - must be an integer.";
            throw InvalidArgumentException.create(errorMessage);
        }
    }
}
