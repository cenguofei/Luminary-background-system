package com.example.routings.search

import com.example.dao.article.SearchArticleDao
import com.example.models.Article
import com.example.models.responses.PageOptions
import com.example.models.responses.pagesData
import com.example.models.tables.DEFAULT_ARTICLE_PAGE_COUNT
import com.example.util.searchArticlePath
import io.ktor.server.routing.*

fun Route.searchArticle() {
    pagesData<Article>(
        pageOptions = PageOptions(
            onIntercept = {
                val searchContent = it.request.queryParameters["searchContent"]
                searchContent.isNullOrBlank()
            },
            onCreateDao = {
                val searchContent = it.request.queryParameters["searchContent"]!!
                SearchArticleDao(searchContent)
            },
        ),
        requestPath = searchArticlePath,
        defaultPageCount = DEFAULT_ARTICLE_PAGE_COUNT
    )
}