package reecejohnson.web.crawler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebCrawlerAppTest {
    @Test
    void shouldPrintApplicationTitle() {
        var app = new WebCrawlerApp();
        assertEquals(app.getTitle(), "Web Crawler.");
    }
}
