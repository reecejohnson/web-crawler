package reecejohnson.web.crawler.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LinkScraper {

    public List<String> scrape(final String url) throws IOException {
        final Document document = Jsoup.connect(url).get();
        return document.select("a[href]")
                .stream()
                .map(link -> link.attr("abs:href"))
                .collect(Collectors.toList());
    }
}
