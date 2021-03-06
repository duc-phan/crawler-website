package crawler.website.service;

import crawler.website.domain.ArticleService;
import crawler.website.parser.ArchivedCaribbeanNewsNowParser;
import crawler.website.parser.CbcParser;
import crawler.website.parser.WpCaribbeanNewsNowParser;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("crawler.website.domain")
@EnableJpaRepositories("crawler.website.domain")
@ComponentScan(basePackages = {"crawler.website"})
public class WebsiteCrawlController implements CommandLineRunner {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(WebsiteCrawlController.class, args);
    }

    @Autowired
    ArticleService articleService;

    @Override
    public void run(String... args) throws Exception {
        // crawlStorageFolder is a folder where intermediate crawl data is store
        String crawlStorageFolder = System.getProperty("java.io.tmpdir");

        // numberOfCrawlers shows the number of concurrent threads that should be initiated for crawling
        int numberOfCrawlers = 10;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);

        // Be polite: Make sure that we don't send more than 1 request per second (1000 milliseconds between requests)
//        config.setPolitenessDelay(1000);

        // You can set the maximum crawl depth here. The default value is -1 for unlimited depth
//        config.setMaxDepthOfCrawling(2);

        // You can set the maximum number of pages to crawl. The default value is -1 for unlimited number of pages
        config.setMaxPagesToFetch(100);

        //Do you want crawler4j to crawl also binary data? example: the contents of pdf, or the metadata of images etc
        config.setIncludeBinaryContentInCrawling(false);

        // Do you need to set a proxy? If so, you can use:
//        config.setProxyHost("proxyserver.example.com");
//        config.setProxyPort(8080);

        // If your proxy also needs authentication:
//        config.setProxyUsername(username);
//        config.getProxyPassword(password);

        // This config parameter can be used to set your crawl to be resumable (meaning that you can resume the crawl from a previously interrupted/crashed crawl)
        // Note: if you enable resuming feature and want to start a fresh crawl, you need to delete the contents of rootFolder manually.
        config.setResumableCrawling(false);

        // Instantiate the controller for this crawl
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        // For each crawl, you need to add some seed urls. These are the first URLs that are fetched and then the crawler starts following links which are found in these pages
//        controller.addSeed(WpCaribbeanNewsNowParser.HOME_PAGE_URL);
//        controller.addSeed(ArchivedCaribbeanNewsNowParser.FIRST_PAGE_URL);
        controller.addSeed(CbcParser.HOME_PAGE_URL);

        WebsiteCrawlerFactory factory = new WebsiteCrawlerFactory(articleService);

        // Start the crawl. This is a blocking operation, meaning that your code will reach the line after this only when crawling is finished.
        controller.startNonBlocking(factory, numberOfCrawlers);
    }
}
