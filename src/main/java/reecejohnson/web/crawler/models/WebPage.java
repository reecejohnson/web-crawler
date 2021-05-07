package reecejohnson.web.crawler.models;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WebPage {
    private final String url;
    private final List<String> links;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(url)
                .append("\n");
        for (String link : links) {
            stringBuilder
                    .append("--> ")
                    .append(link)
                    .append("\n");
        }
        return stringBuilder.toString();
    }
}
