package crawler.website.service;

import crawler.website.constant.WebsiteUrlConstant;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.measure.unit.SystemOfUnits;
import java.util.Set;
import java.util.regex.Pattern;

public class WebsiteCrawler extends WebCrawler {

    private static final Pattern EXTENSIONS_FILTER = Pattern.compile(".*\\.(bmp|gif|jpg|png|pdf|js|css|mp3|mp4|zip|gz)$");

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        if (EXTENSIONS_FILTER.matcher(href).matches()) {
            return false;
        }

        // Only accept the url if it is in the "www.ics.uci.edu" domain and protocol is "http".
        return href.startsWith(WebsiteUrlConstant.CARIBBEAN_NEW_NOW);
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();
            Document doc = Jsoup.parseBodyFragment(html);
            Elements header = doc.getElementsByClass("td-post-title");
            Elements content = doc.getElementsByClass("td-post-content");

            System.out.println("URL:");
            System.out.println("    " + page.getWebURL().getURL());

            System.out.println("Post Header:");
            System.out.println("    " + header.text());

            System.out.println("Content:");
            System.out.println("    " + content.text());

//            System.out.println("Image Link:");
//            System.out.println("    ");
        }
    }
}
