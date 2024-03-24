package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.util.articlesRootPath
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureArticleRouting() {
    val articleDao = ArticleDao
    routing {
        route(articlesRootPath) {
            createArticle(articleDao)
            getArticleById(articleDao)
            updateArticle(articleDao)
            deleteArticleById(articleDao)
            pageArticles(articleDao)
            publicArticlesOfUser(articleDao)
            privacyArticlesOfUser(articleDao)
            getAllArticlesOfUserCollected()
            getAllArticlesOfUserLiked()
            pageFriendsArticles()
        }
    }
}
