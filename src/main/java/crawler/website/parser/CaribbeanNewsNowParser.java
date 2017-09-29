package crawler.website.parser;

import java.util.Date;

public interface CaribbeanNewsNowParser {
    String parseArticleImageLink();
    String parseArticleContent();
    String parseArticleTitle();
    Date parseArticlePublishDate();
    boolean hasArticle();
}
