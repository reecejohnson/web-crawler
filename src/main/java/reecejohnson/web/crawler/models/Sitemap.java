package reecejohnson.web.crawler.models;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Sitemap {
    List<WebPage> webPages;

    @Override
    public String toString() {
        StringBuilder siteMap = new StringBuilder();
        for (WebPage webPage : webPages) {
            siteMap
                    .append(webPage)
                    .append("\n");
        }
        return siteMap.toString();
    }
}
