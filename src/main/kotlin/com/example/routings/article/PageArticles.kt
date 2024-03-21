package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.models.responses.pagesData
import com.example.models.tables.DEFAULT_ARTICLE_PAGE_COUNT
import com.example.util.pageArticlesPath
import io.ktor.server.routing.*

fun Route.pageArticles(articleDao: ArticleDao) {
    pagesData(
        dao = articleDao,
        requestPath = pageArticlesPath,
        defaultPageCount = DEFAULT_ARTICLE_PAGE_COUNT
    )
}