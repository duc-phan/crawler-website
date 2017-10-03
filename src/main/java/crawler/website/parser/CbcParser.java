package crawler.website.parser;

import crawler.website.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.util.Date;

public class CbcParser implements ArticleParser {
    public static final String HOME_PAGE_URL                    = "http://www.cbc.bb/";
    public static final String SUB_DOMAIN                       = "www";
    public static final String DOMAIN                           = "cbc.bb";

    private static final String ARTICLE_TITLE_CSS               = "itemTitle";
    private static final String ARTICLE_CONTENT_CSS             = "mn-itemText";
    private static final String ARTICLE_PUBLISH_DATE_CSS        = "itemDateCreated";
    private static final String ARTICLE_IMAGE_CSS               = "itemImage";

    private static final String ARTICLE_DATE_FORMAT             = "MMMM dd, yyyy";

    private String html;
    private Document doc;
    private Element title;
    private Element article;

    public CbcParser(String html) {
        this.html = html;
        Document doc = Jsoup.parseBodyFragment(html);
        article = doc.getElementsByClass(ARTICLE_CONTENT_CSS).first();
    }

    @Override
    public String parseArticleImageLink() {
        StringBuilder images = new StringBuilder();
        Elements elementImages = article.getElementsByClass(ARTICLE_IMAGE_CSS);
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

    @Override
    public String parseArticleContent() {
        StringBuilder textBuilder = new StringBuilder();
        Elements elementTexts = article.getElementsByClass(ARTICLE_CONTENT_CSS);
        if (elementTexts != null) {
            elementTexts.forEach(elementText -> {
                textBuilder.append(elementText.text());
            });
        }

        return textBuilder.toString();
    }

    @Override
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

    @Override
    public Date parseArticlePublishDate() {
        String dateText = "";//article.getElementsByClass(ARTICLE_PUBLISH_DATE_CSS).get(0).text();
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
