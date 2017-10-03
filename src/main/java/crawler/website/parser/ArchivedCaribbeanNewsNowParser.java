package crawler.website.parser;

import crawler.website.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.util.Date;

public class ArchivedCaribbeanNewsNowParser implements ArticleParser {
    public static final String HOME_PAGE_URL                    = "http://www.caribbeannewsnow.com";
    public static final String FIRST_PAGE_URL                   = "http://www.caribbeannewsnow.com/archive.php";
    public static final String DOMAIN                           = "caribbeannewsnow.com";
    public static final String SUB_DOMAIN                       = "www";

    private static final String ARTICLE_TITLE_CSS               = "tbody > tr > td.title";
    private static final String ARTICLE_CONTENT_TAG             = "article";
    private static final String ARTICLE_PUBLISH_DATE_CSS        = "tbody > tr > td.textsmall";

    private static final String ARTICLE_DATE_FORMAT             = "MMMM dd, yyyy";

    private Document doc;
    private Element title;
    private Element article;

    public ArchivedCaribbeanNewsNowParser(String html) {
        doc = Jsoup.parseBodyFragment(html);
        title = doc.select(ARTICLE_TITLE_CSS).first();
        article = doc.getElementsByClass(ARTICLE_CONTENT_TAG).first();
    }

    @Override
    public String parseArticleImageLink() {
        StringBuilder images = new StringBuilder();
        if (article != null) {
            Elements imageTags = article.select("img");
            if (imageTags != null) {
                imageTags.forEach(element -> {
                    String imageLink = element.attr("src");
                    if (!imageLink.startsWith("http")) {
                        imageLink = HOME_PAGE_URL + imageLink;
                    }
                    imageLink += "\n";
                    images.append(imageLink);
                });
            }
        }

        return images.toString();
    }

    @Override
    public String parseArticleContent() {
        StringBuilder textBuilder = new StringBuilder();
        if (article != null) {
            textBuilder.append(article.text());
        }

        return textBuilder.toString();
    }

    @Override
    public String parseArticleTitle() {
        StringBuilder textBuilder = new StringBuilder();
        if (title != null) {
            textBuilder.append(title.text());
        }

        return textBuilder.toString();
    }

    @Override
    public Date parseArticlePublishDate() {
        String dateText = doc.select(ARTICLE_PUBLISH_DATE_CSS).get(0).text();
        dateText = dateText.substring("Published on ".length());
        Date publishDate = new Date();
        try {
            publishDate = DateUtils.convertStringToDate(dateText, ARTICLE_DATE_FORMAT);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return publishDate;
    }

    @Override
    public boolean hasArticle() {
        return article != null;
    }
}
