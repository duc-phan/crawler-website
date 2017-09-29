package crawler.website.service;

import crawler.website.domain.ArticleService;
import edu.uci.ics.crawler4j.crawler.CrawlController.WebCrawlerFactory;
import edu.uci.ics.crawler4j.crawler.WebCrawler;

public class WebsiteCrawlerFactory implements WebCrawlerFactory {
    ArticleService articleService;

    public WebsiteCrawlerFactory(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public WebCrawler newInstance() throws Exception {
        return new WebsiteCrawler(articleService);
    }
}
