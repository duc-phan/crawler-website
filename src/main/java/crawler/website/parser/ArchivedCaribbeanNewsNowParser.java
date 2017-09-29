package crawler.website.parser;

import crawler.website.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.util.Date;

public class ArchivedCaribbeanNewsNowParser implements  CaribbeanNewsNowParser{
    public static final String HOME_PAGE_URL                    = "http://www.caribbeannewsnow.com";
    public static final String FIRST_PAGE_URL                   = "http://www.caribbeannewsnow.com/archive.php";
    public static final String SUB_DOMAIN                       = "www";

    private static final String ARTICLE_TITLE_CSS               = "tbody > tr > td.title";
    private static final String ARTICLE_CONTENT_TAG             = "article";
    private static final String ARTICLE_PUBLISH_DATE_CSS        = "tbody > tr > td.textsmall";

    private static final String ARTICLE_DATE_FORMAT             = "E, dd MMMM yyyy HH:mm";

    private Document doc;
    private Elements titles;
    private Elements articles;

    public ArchivedCaribbeanNewsNowParser(String html) {
        doc = Jsoup.parseBodyFragment(html);
        titles = doc.select(ARTICLE_TITLE_CSS);
        articles = doc.getElementsByTag(ARTICLE_CONTENT_TAG);
    }

    @Override
    public String parseArticleImageLink() {
        StringBuilder images = new StringBuilder();
        if (articles != null) {
            articles.forEach(elementImage -> {
                Elements imageTags = elementImage.select("img");
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
            });
        }

        return images.toString();
    }

    @Override
    public String parseArticleContent() {
        StringBuilder textBuilder = new StringBuilder();
        if (articles != null) {
            articles.forEach(elementText -> {
                textBuilder.append(elementText.text());
            });
        }

        return textBuilder.toString();
    }

    @Override
    public String parseArticleTitle() {
        StringBuilder textBuilder = new StringBuilder();
        if (titles != null) {
            titles.forEach(elementTitle -> {
                textBuilder.append(elementTitle.text());
            });
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
        }

        return publishDate;
    }

    @Override
    public boolean hasArticle() {
        return articles != null && !articles.isEmpty();
    }
}
