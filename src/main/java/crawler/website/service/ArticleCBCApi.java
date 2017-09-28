package crawler.website.service;

import crawler.website.domain.Article;
import crawler.website.util.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by DUNGDV on 8/8/2017.
 */
public class ArticleCBCApi {
    private static final String ARTICLE_CONTENT_TAG_CLASS = "itemBody";
    private static final String ARTICLE_TEXT_TAG_CLASS = "mn-itemText";
    private static final String ARTICLE_TITLE_TAG_CLASS = "itemTitle";
    private static final String ARTICLE_IMAGE_TAG_CLASS = "itemImageBlock";

    private static final String ARTICLE_ITEM_TAG_CLASS = "latestItemView";
    private static final String ARTICLE_DATE_TAG_CLASS = "latestItemDateCreated";
    private static final String ARTICLE_DATE_FORMAT = "E, dd MMMM yyyy HH:mm";

    private static final String RADIO_ARTICLE_ITEM_TAG_CLASS = "itemContainer";
    private static final String RADIO_ARTICLE_DATE_TAG_CLASS = "itemDate";
    private static final String RADIO_ARTICLE_DATE_FORMAT = "MMM dd, yyyy";

    private static final String ARTICLE_LINK_REGEX = "/item/";
    public static final String PAGE_HOME_URL = "http://www.cbc.bb";
    private static final String PAGE_DOMAIN = "www.cbc.bb";
    private final static Pattern FILE_URL_PATTERN = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz))$");

    public List<String> parseHomePage(String homeHtml) {
        Set<String> setCategory = new HashSet<>();
        Document doc = Jsoup.parse(homeHtml);

        Elements links = doc.select("a");
        if (links == null) {
            return Collections.EMPTY_LIST;
        }

        links.forEach(element -> {
            String categoryUrl = element.attr("href");
            if (isCategoryLink(categoryUrl)) {
                if (!categoryUrl.startsWith("http")) {
                    categoryUrl = PAGE_HOME_URL + categoryUrl;
                }

                setCategory.add(categoryUrl);
            }
        });

        return setCategory.stream().collect(Collectors.toList());
    }

    public List<Article> parseCategoryPage(String categoryHtml) {
        Document doc = Jsoup.parse(categoryHtml);
        List<Article> articles;

        articles = parseArticles(doc, ARTICLE_ITEM_TAG_CLASS, ARTICLE_DATE_TAG_CLASS, ARTICLE_DATE_FORMAT);
        if (!CollectionUtils.isEmpty(articles)) {
            return articles;
        }


        articles = parseArticles(doc, RADIO_ARTICLE_ITEM_TAG_CLASS, RADIO_ARTICLE_DATE_TAG_CLASS, RADIO_ARTICLE_DATE_FORMAT);
        if (!CollectionUtils.isEmpty(articles)) {
            return articles;
        }

        return Collections.EMPTY_LIST;
    }

    private List<Article> parseArticles(Document doc, String articleItemClass, String dateTagClass, String dateFormat) {
        List<Article> articles = new ArrayList<>();
        Elements articleItems = doc.getElementsByClass(articleItemClass);
        if (!CollectionUtils.isEmpty(articleItems)) {
            articleItems.forEach(articleItem -> {
                Elements tagAs = articleItem.getElementsByTag("a");
                Elements tagDates = articleItem.getElementsByClass(dateTagClass);
                if (!CollectionUtils.isEmpty(tagAs) && !CollectionUtils.isEmpty(tagDates)) {
                    String articleUrl = tagAs.get(0).attr("href");
                    String dateText = tagDates.get(0).text();

                    if (articleUrl.contains(ARTICLE_LINK_REGEX) && isCBCLink(articleUrl)) {
                        Article article = new Article();
                        article.setFetched(false);

                        if (!articleUrl.startsWith("http")) {
                            article.setUrl(PAGE_HOME_URL + articleUrl);
                        } else {
                            article.setUrl(articleUrl);
                        }

                        Date publishDate;
                        try {
                            publishDate = DateUtils.convertStringToDate(dateText, dateFormat);
                        } catch (ParseException e) {
                            publishDate = new Date();
                        }
                        article.setPublishDate(publishDate);

                        String shortUrl = article.getUrl().substring(article.getUrl().indexOf(ARTICLE_LINK_REGEX) + ARTICLE_LINK_REGEX.length());
                        article.setShortUrl(shortUrl);

                        articles.add(article);
                    }
                }
            });
        }

        return articles;
    }


    public Article parseArticleDetail(String articleHtml, Article article) {
        Document doc = Jsoup.parse(articleHtml);
        Elements elements = doc.getElementsByClass(ARTICLE_CONTENT_TAG_CLASS);
        if (elements.size() > 0) {
            Element elementArticle = elements.get(0);
            String textContent = findTextContentOfArticle(elementArticle);
            List<String> images = findImagesOfArticle(elementArticle);
            String title = findTitleOfArticle(elementArticle);

            article.setContent(textContent);
            article.setImageLinks(images);
            article.setTitle(title);
        }

        return article;
    }

    private List<String> findImagesOfArticle(Element elementArticle) {
        List<String> images = new ArrayList<>();
        Elements elementImages = elementArticle.getElementsByClass(ARTICLE_IMAGE_TAG_CLASS);
        if (elementImages != null) {
            elementImages.forEach(elementImage -> {
                Elements imageTags = elementImage.select("img");
                if (imageTags != null) {
                    imageTags.forEach(element -> {
                        String imageLink = element.attr("src");
                        if (!imageLink.startsWith("http")) {
                            imageLink = PAGE_HOME_URL + imageLink;
                        }
                        images.add(imageLink);
                    });
                }
            });
        }

        Elements elementTexts = elementArticle.getElementsByClass(ARTICLE_TEXT_TAG_CLASS);
        if (elementTexts != null) {
            elementTexts.forEach(elementText -> {
                Elements imageTags = elementText.select("img");
                if (imageTags != null) {
                    imageTags.forEach(element -> {
                        String imageLink = element.attr("src");
                        if (!imageLink.startsWith("http")) {
                            imageLink = PAGE_HOME_URL + imageLink;
                        }
                        images.add(imageLink);
                    });
                }
            });
        }

        return images;
    }

    private String findTextContentOfArticle(Element elementArticle) {
        StringBuilder textBuilder = new StringBuilder();
        Elements elementTexts = elementArticle.getElementsByClass(ARTICLE_TEXT_TAG_CLASS);
        if (elementTexts != null) {
            elementTexts.forEach(elementText -> {
                textBuilder.append(elementText.text());
            });
        }

        return textBuilder.toString();
    }

    private String findTitleOfArticle(Element elementArticle) {
        StringBuilder textBuilder = new StringBuilder();
        Elements elementTitles = elementArticle.getElementsByClass(ARTICLE_TITLE_TAG_CLASS);
        if (elementTitles != null) {
            elementTitles.forEach(elementTitle -> {
                textBuilder.append(elementTitle.text());
            });
        }

        return textBuilder.toString();
    }

    private boolean isCBCLink(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }

        return !(url.startsWith("http") && !url.contains(PAGE_DOMAIN));
    }

    private boolean isHomeLink(String url) {
        return PAGE_HOME_URL.equals(url);
    }

    private boolean isCategoryLink(String url) {
        return isCBCLink(url) && !url.contains(ARTICLE_LINK_REGEX) && !isHomeLink(url) && !FILE_URL_PATTERN.matcher(url).matches();
    }
}
