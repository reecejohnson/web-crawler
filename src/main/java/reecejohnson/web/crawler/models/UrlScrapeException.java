package reecejohnson.web.crawler.models;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrlScrapeException extends Exception {

    public UrlScrapeException(final String message, final Exception exception) {
        super(message, exception);
    }

    public static UrlScrapeException create(final String url, final Exception exception) {
        String errorMessage = String.format("Error scraping %s with exception: %s", url, exception.getMessage());
        log.error(errorMessage);
        return new UrlScrapeException(errorMessage, exception);
    }
}
