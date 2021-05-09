package reecejohnson.web.crawler.crawler;

import lombok.extern.slf4j.Slf4j;
import reecejohnson.web.crawler.models.UrlScrapeException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LinkScraper {

    public List<String> scrape(final String url) throws UrlScrapeException {
        try {
            final Document document = Jsoup.connect(url).get();
            return document.select("a[href]")
                    .stream()
                    .map(link -> link.attr("abs:href"))
                    .collect(Collectors.toList());
        } catch (Exception exception) {
            throw UrlScrapeException.create(url, exception);
        }
    }
}
