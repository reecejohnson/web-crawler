package reecejohnson.web.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reecejohnson.web.crawler.crawler.Crawler;
import reecejohnson.web.crawler.models.InvalidArgumentException;
import reecejohnson.web.crawler.models.WebPage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class WebCrawlerApp implements CommandLineRunner {

    private final Crawler crawler;

    public static void main(String[] args) {
        SpringApplication.run(WebCrawlerApp.class, args);
    }

    @Override
    public void run(String... args) throws IOException, InvalidArgumentException {
        String url = getUrlFromArguments(args);

        final WebPage webPage = crawler.crawl(url);

        System.out.println(webPage.toString());
    }

    private String getUrlFromArguments(String... args) throws InvalidArgumentException {
        try {
            return args[0];
        } catch (Exception exception) {
            throw InvalidArgumentException.create();
        }
    }
}
