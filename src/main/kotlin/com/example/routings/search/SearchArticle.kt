package com.example.routings.search

import com.example.dao.article.SearchArticleDao
import com.example.models.responses.pagesData
import com.example.models.tables.DEFAULT_ARTICLE_PAGE_COUNT
import com.example.util.pageFriendsArticlesPath
import com.example.util.searchArticlePath
import io.ktor.server.routing.*

fun Route.searchArticle() {
    pagesData(
        onCall = {
            val searchContent = it.request.queryParameters["searchContent"]
            searchContent.isNullOrBlank()
        },
        createDao = {
            val searchContent = it.request.queryParameters["searchContent"]!!
            SearchArticleDao(searchContent)
        },
        requestPath = searchArticlePath,
        defaultPageCount = DEFAULT_ARTICLE_PAGE_COUNT
    )
}