package reecejohnson.web.crawler;

public class WebCrawlerApp {
    public String getTitle() {
        return "Web Crawler.";
    }

    public static void main(String[] args) {
        System.out.println(new WebCrawlerApp().getTitle());
    }
}
