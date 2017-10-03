package crawler.website.parser;

import java.util.Date;

public interface ArticleParser {
    String parseArticleImageLink();
    String parseArticleContent();
    String parseArticleTitle();
    Date parseArticlePublishDate();
    boolean hasArticle();
}
