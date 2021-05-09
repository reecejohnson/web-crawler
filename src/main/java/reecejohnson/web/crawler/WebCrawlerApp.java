package reecejohnson.web.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reecejohnson.web.crawler.crawler.CrawlerOrchestrator;
import reecejohnson.web.crawler.models.InvalidArgumentException;
import reecejohnson.web.crawler.models.Sitemap;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class WebCrawlerApp implements CommandLineRunner {

    private final CrawlerOrchestrator crawlerOrchestrator;

    public static void main(String[] args) {
        SpringApplication.run(WebCrawlerApp.class, args);
    }

    @Override
    public void run(String... args) throws InvalidArgumentException {
        String url = getUrlFromArguments(args);

        final Sitemap sitemap = crawlerOrchestrator.start(url);

        String finishedCrawlMessage = String.format("Finished crawl: %s has %s crawlable pages", url, sitemap.getWebPages().size());
        log.info(finishedCrawlMessage);
        System.out.println(finishedCrawlMessage);
        String sitemapString = sitemap.toString();
        log.info(sitemapString);
        System.out.println(sitemapString);
    }

    private String getUrlFromArguments(String... args) throws InvalidArgumentException {
        try {
            return args[0];
        } catch (Exception exception) {
            throw InvalidArgumentException.create();
        }
    }
}
