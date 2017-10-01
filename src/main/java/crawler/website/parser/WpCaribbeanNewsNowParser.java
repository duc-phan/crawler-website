package crawler.website.parser;

import crawler.website.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.util.Date;

public class WpCaribbeanNewsNowParser implements  CaribbeanNewsNowParser{
    public static final String HOME_PAGE_URL                    = "http://wp.caribbeannewsnow.com/";
    public static final String SUB_DOMAIN                       = "wp";

    private static final String ARTICLE_TAG                     = "article";
    private static final String ARTICLE_TITLE_CSS               = "entry-title";
    private static final String ARTICLE_IMAGE_CSS               = "entry-thumb";
    private static final String ARTICLE_CONTENT_CSS             = "td-post-content";
    private static final String ARTICLE_IMAGE_CAPTION_CSS       = "wp-caption-text";
    private static final String ARTICLE_PUBLISH_DATE_CSS        = "td-post-date";
    private static final String PRINT_BUTTON_CSS                = ".h5ab-print-button-container";
    private static final String EMAIL_TO_FRIEND_CSS             = "div > a[title='Email To Friend']";

    private static final String ARTICLE_DATE_FORMAT             = "MMMM dd, yyyy";

    private String html;
    private Element article;

    public WpCaribbeanNewsNowParser(String html) {
        this.html = html;
        Document doc = Jsoup.parseBodyFragment(html);
        article = doc.getElementsByTag("article").first();
    }

    public String parseArticleImageLink() {
        StringBuilder images = new StringBuilder();
        Elements elementImages = article.getElementsByClass(ARTICLE_CONTENT_CSS);
        if (elementImages != null) {
            elementImages.forEach(elementImage -> {
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

    public String parseArticleContent() {
        StringBuilder textBuilder = new StringBuilder();
        Elements elementTexts = article.getElementsByClass(ARTICLE_CONTENT_CSS);
        if (elementTexts != null) {
            elementTexts.forEach(elementText -> {
                elementText.select(PRINT_BUTTON_CSS).remove();
                elementText.select(EMAIL_TO_FRIEND_CSS).remove();
                textBuilder.append(elementText.text());
            });
        }

        return textBuilder.toString();
    }

    public String parseArticleTitle() {
        StringBuilder textBuilder = new StringBuilder();
        Elements elementTitles = article.getElementsByClass(ARTICLE_TITLE_CSS);
        if (elementTitles != null) {
            elementTitles.forEach(elementTitle -> {
                textBuilder.append(elementTitle.text());
            });
        }

        return textBuilder.toString();
    }

    public Date parseArticlePublishDate() {
        String dateText = article.getElementsByClass(ARTICLE_PUBLISH_DATE_CSS).get(0).text();
        Date publishDate = new Date();
        try {
            publishDate = DateUtils.convertStringToDate(dateText, ARTICLE_DATE_FORMAT);
        } catch (ParseException e) {
        }

        return publishDate;
    }

    @Override
    public boolean hasArticle() {
        return article != null;
    }
}
