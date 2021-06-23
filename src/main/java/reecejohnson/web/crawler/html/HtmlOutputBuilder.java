package reecejohnson.web.crawler.html;

import lombok.extern.slf4j.Slf4j;
import reecejohnson.web.crawler.models.Sitemap;
import reecejohnson.web.crawler.models.WebPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Slf4j
@Component
public class HtmlOutputBuilder {

    private Document document;

    public HtmlOutputBuilder() throws IOException {
        document = Jsoup.parse(getHtmlFIleAsString("template.html"));
    }

    public void buildOutputFile(final Sitemap sitemap) {
        try {
            appendTextToElement("#url", sitemap.getWebPages().get(0).getUrl());
            appendTextToElement("#total-pages", String.valueOf(sitemap.getWebPages().size()));

            for (WebPage webPage : sitemap.getWebPages()) {
                String linkListElement = buildLinkListElement(webPage);
                appendLinkResultToLinkResults(linkListElement);
            }

            Files.write(Paths.get("output/result.html"), document.toString().getBytes());
        } catch (Exception exception) {
            log.error("Html template error: {}", exception.getMessage());
        }
    }

    private String getHtmlFIleAsString(String fileName) throws IOException {
        return new String(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(fileName)).readAllBytes());
    }

    private void appendTextToElement(final String selector, final String text) {
        Element div = document.select(selector).first();
        div.appendText(text);
    }

    private void appendLinkResultToLinkResults(final String element) {
        Element div = document.select("#link-results").first();
        div.append(element);
    }

    private String buildLinkListElement(final WebPage webPage) {
        StringBuilder list = new StringBuilder();

        list.append("<div class=\"bg-white shadow rounded px-6 py-4 w-full\">")
                .append("<div class=\"flex items-center\">")
                .append("<p class=\"text-xl font-semibold\">")
                .append(webPage.getUrl())
                .append("</p>")
                .append("<span style=\"background-color: #14233c\" class=\"text-sm text-white ml-4 rounded-lg px-4 py-1\">")
                .append(webPage.getLinks().size()).append(" links")
                .append("</span>")
                .append("</div>");
        list.append("<hr class=\"my-4\"/>");
        list.append("<div class=\"grid gap-4\">");
        for (String link : webPage.getLinks()) {
            list.append("<p>").append(link).append("</p>");
        }
        list.append("</div>");
        list.append("</div>");

        return list.toString();
    }
}
