package crawler.website.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by DUNGDV on 8/8/2017.
 */
public class Article {

    private String url;

    /*
    * shortUrl identify for article
    * Example:
    * url = "http://www.cbc.bb/index.php/news/item/1644-trump-threatens-fury-against-n-korea"
    * shortUrl = "1644-trump-threatens-fury-against-n-korea"
    * */
    private String shortUrl;

    private boolean isFetched;

    private String content;

    private List<String> imageLinks;

    private String imageLink;

    private String author;

    private String title;

    private Date publishDate;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFetched() {
        return isFetched;
    }

    public void setFetched(boolean fetched) {
        isFetched = fetched;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(List<String> imageLinks) {
        this.imageLinks = imageLinks;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public void deserializeImageLink() {
        if (imageLink != null && !imageLink.isEmpty()) {
            imageLinks = Arrays.asList(imageLink.split(","));
        } else {
            imageLinks = Collections.EMPTY_LIST;
        }
    }

    public void serializeImageLink() {
        if (imageLinks != null && imageLinks.size() > 0) {
            imageLink = String.join(",", imageLinks);
        } else {
            imageLink = "";
        }
    }

}
