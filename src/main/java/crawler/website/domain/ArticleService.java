package crawler.website.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleService {
    @Autowired
    ArticleRepository articleRepository;

    @Transactional
    public void saveArticle(Article article) {
        articleRepository.save(article);
    }
}
