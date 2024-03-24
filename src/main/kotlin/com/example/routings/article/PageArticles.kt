package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.dao.article.FriendsArticleDao
import com.example.models.responses.pagesData
import com.example.models.tables.DEFAULT_ARTICLE_PAGE_COUNT
import com.example.util.*
import io.ktor.server.routing.*

fun Route.pageArticles(articleDao: ArticleDao) {
    pagesData(
        createDao = { articleDao },
        requestPath = pageArticlesPath,
        defaultPageCount = DEFAULT_ARTICLE_PAGE_COUNT
    )
}

/**
 * 需要传用户的id
 */
fun Route.pageFriendsArticles() {
    pagesData(
        createDao = {
            val loginUserId = it.request.queryParameters["loginUserId"]?.toLong() ?: Long.Default
            FriendsArticleDao(loginUserId)
        },
        requestPath = pageFriendsArticlesPath,
        defaultPageCount = DEFAULT_ARTICLE_PAGE_COUNT
    )
}


