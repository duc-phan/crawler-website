package crawler.website.service;

import crawler.website.domain.Article;
import crawler.website.domain.ArticleService;
import crawler.website.parser.ArchivedCaribbeanNewsNowParser;
import crawler.website.parser.CaribbeanNewsNowParser;
import crawler.website.parser.WpCaribbeanNewsNowParser;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.regex.Pattern;


public class WebsiteCrawler extends WebCrawler {

    private static final Pattern EXTENSIONS_FILTER = Pattern.compile(".*\\.(bmp|gif|jpg|png|pdf|js|css|mp3|mp4|zip|gz)$");
    private static final Logger logger = LoggerFactory.getLogger(WebsiteCrawler.class);
    private static int number = 1;

    private ArticleService articleService;

    public WebsiteCrawler(ArticleService articleService) {
        this.articleService = articleService;
    }

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
        return href.startsWith(WpCaribbeanNewsNowParser.HOME_PAGE_URL) || href.startsWith(ArchivedCaribbeanNewsNowParser.HOME_PAGE_URL);
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String html = htmlParseData.getHtml();
            CaribbeanNewsNowParser parser;

            String subDomain = page.getWebURL().getSubDomain();
            if (WpCaribbeanNewsNowParser.SUB_DOMAIN.equals(subDomain)) {
                parser = new WpCaribbeanNewsNowParser(html);
            } else if (ArchivedCaribbeanNewsNowParser.SUB_DOMAIN.equals(subDomain)) {
                parser = new ArchivedCaribbeanNewsNowParser(html);
            } else {
                logger.info("--------- NOT SUPPORT SUB DOMAIN --------------------------------");
                return;
            }

            if(!parser.hasArticle()) {
                logger.info("--------- NO ARTICLE ---------------------------------");
                return;
            }

            Article article = new Article();
            article.setCreatedAt(new Date());
            article.setUrl(page.getWebURL().getURL());
            article.setShortUrl(page.getWebURL().getPath());
            article.setTitle(parser.parseArticleTitle());
            article.setPublishDate(parser.parseArticlePublishDate());
            article.setContent(parser.parseArticleContent());
            article.setImageLink(parser.parseArticleImageLink());

            articleService.saveArticle(article);

            logger.info("Article URL:");
            logger.info("    " + page.getWebURL().getURL());
            logger.info("Article Title:");
            logger.info("    " + parser.parseArticleTitle());
            logger.info("Article Publish Date:");
            logger.info("   " + parser.parseArticlePublishDate());
            logger.info("Article Content:");
            logger.info("    " + parser.parseArticleContent());
            logger.info("Article Image Links:");
            logger.info("   " + parser.parseArticleImageLink());
        }
    }
}
