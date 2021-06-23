package reecejohnson.web.crawler.url;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

import static org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS;

@Component
@Slf4j
public class UrlUtilities {

    public String cleanseUrl(final String url) {
        String removeInternalPageSectionLink = removeInternalPageSectionLink(url);
        String lowerCaseLink = removeInternalPageSectionLink.toLowerCase();
        return removeTrailingSlash(lowerCaseLink);
    }

    public boolean isUrlValid(final String url) {
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes, ALLOW_LOCAL_URLS);
        return urlValidator.isValid(url);
    }

    public boolean areUrlsTheSameDomain(final String firstUrl, final String secondUrl) {
        try {
            final URI uri = new URI(firstUrl);
            final URI secondUri = new URI(secondUrl);

            final String formattedUrl = removeWwwPrefix(uri);
            final String secondFormattedUrl = removeWwwPrefix(secondUri);

            return formattedUrl.equals(secondFormattedUrl);
        } catch (URISyntaxException exception) {
            log.error("Unable to read URL with exception: {}", exception.getMessage());
            return false;
        }
    }

    private String removeWwwPrefix(final URI uri) {
        String wwwPrefix = "www.";
        var host = uri.getHost();

        if (host.startsWith(wwwPrefix)) {
            return host.substring(4);
        }

        return host;
    }

    private String removeTrailingSlash(final String url) {
        if (url.charAt(url.length() - 1) == '/') {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    private String removeInternalPageSectionLink(final String url) {
        int indexOfHash = url.lastIndexOf('#');
        if (indexOfHash == -1) {
            return url;
        }
        return url.substring(0, indexOfHash);
    }
}
