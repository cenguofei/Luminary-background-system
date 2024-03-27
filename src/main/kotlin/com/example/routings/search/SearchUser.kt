package com.example.routings.search

import com.example.dao.user.SearchUserDao
import com.example.models.responses.pagesData
import com.example.models.tables.DEFAULT_ARTICLE_PAGE_COUNT
import com.example.util.searchUserPath
import io.ktor.server.routing.*

fun Route.searchUser() {
    pagesData(
        onCall = {
            val searchContent = it.request.queryParameters["searchContent"]
            searchContent.isNullOrBlank()
        },
        createDao = {
            val searchContent = it.request.queryParameters["searchContent"]!!
            SearchUserDao(searchContent)
        },
        requestPath = searchUserPath,
        defaultPageCount = DEFAULT_ARTICLE_PAGE_COUNT
    )
}