package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.dao.article.FriendsArticleDao
import com.example.dao.article.recommend.RecommendDao
import com.example.dao.article.recommend.RecommendManager
import com.example.models.Article
import com.example.models.responses.PageOptions
import com.example.models.responses.pagesData
import com.example.models.tables.DEFAULT_ARTICLE_PAGE_COUNT
import com.example.util.*
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
            val wishPage = it.request.queryParameters["wishPage"]?.toInt() ?: Int.Default
            if (wishPage == 0) {
                //当用户刷新或者重新进入app时更新缓存
                RecommendManager.remove(loginUserId)
                "RecommendArticlesManager.remove $loginUserId".logi("update_cache")
            }
            RecommendManager.getOrPut(loginUserId) {
                RecommendDao(loginUserId)
            }
        },
        requestPath = pageRecommendedArticlesPath,
        defaultPageCount = DEFAULT_ARTICLE_PAGE_COUNT
    )
}


