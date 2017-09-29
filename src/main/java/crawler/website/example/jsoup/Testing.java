package crawler.website.example.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class Testing {
    public static void main(String[] args) {
        String url = "http://wp.caribbeannewsnow.com/2017/09/28/agriculture-sector-dominica-destroyed-hurricane-maria/";
        try {
            Document doc = Jsoup.connect(url).get();
            Element article = doc.getElementsByClass("td-post-content").first();
            System.out.println(article.select(".h5ab-print-button-container").text());
            article.select(".h5ab-print-button-container").remove();
            System.out.println(article.select("div > a[title='Email To Friend']").text());
            article.select("div > a[title='Email To Friend']").remove();
            System.out.println(article.text());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
