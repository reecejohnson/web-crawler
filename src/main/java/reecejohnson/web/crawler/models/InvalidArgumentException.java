package reecejohnson.web.crawler.models;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidArgumentException extends Exception {

    public InvalidArgumentException(final String message) {
        super(message);
    }

    public static InvalidArgumentException create(final String message) {
        log.error(message);
        return new InvalidArgumentException(message);
    }
}
