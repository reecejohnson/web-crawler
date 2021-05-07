package reecejohnson.web.crawler.models;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidArgumentException extends Exception {

    public InvalidArgumentException(final String message) {
        super(message);
    }

    public static InvalidArgumentException create() {
        String errorMessage = "Invalid arguments provided";
        log.error(errorMessage);
        return new InvalidArgumentException(errorMessage);
    }
}
