package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.dao.article.FriendsArticleDao
import com.example.dao.article.recommend.RecommendArticlesDao
import com.example.dao.article.recommend.RecommendArticlesManager
import com.example.models.Article
import com.example.models.responses.PageOptions
import com.example.models.responses.pagesData
import com.example.models.tables.DEFAULT_ARTICLE_PAGE_COUNT
import com.example.util.Default
import com.example.util.pageArticlesPath
import com.example.util.pageFriendsArticlesPath
import com.example.util.pageRecommendedArticlesPath
import io.ktor.server.routing.*

fun Route.pageAllArticles(articleDao: ArticleDao) {
    pagesData<Article>(
        requestPath = pageArticlesPath,
        defaultPageCount = DEFAULT_ARTICLE_PAGE_COUNT,
        pageOptions = PageOptions { articleDao }
    )
}

fun Route.pageFriendsArticles() {
    pagesData<Article>(
        pageOptions = PageOptions {
            val loginUserId = it.request.queryParameters["loginUserId"]?.toLong() ?: Long.Default
            FriendsArticleDao(loginUserId)
        },
        requestPath = pageFriendsArticlesPath,
        defaultPageCount = DEFAULT_ARTICLE_PAGE_COUNT
    )
}

fun Route.pageRecommendedArticles() {
    pagesData<Article>(
        pageOptions = PageOptions {
            val loginUserId = it.request.queryParameters["loginUserId"]?.toLong() ?: Long.Default
            RecommendArticlesManager.getOrPut(loginUserId) {
                RecommendArticlesDao(loginUserId)
            }
        },
        requestPath = pageRecommendedArticlesPath,
        defaultPageCount = DEFAULT_ARTICLE_PAGE_COUNT
    )
}


