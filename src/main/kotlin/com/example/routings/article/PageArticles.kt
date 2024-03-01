package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.models.Article
import com.example.models.responses.Page
import com.example.models.responses.PageResponse
import com.example.models.responses.calculatePageSize
import com.example.models.responses.pagesData
import com.example.models.tables.DEFAULT_ARTICLE_PAGE_COUNT
import com.example.util.logd
import com.example.util.pageArticlesPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.pageArticles(articleDao: ArticleDao) {
    pagesData(
        dao = articleDao,
        requestPath = pageArticlesPath,
        defaultPageCount = DEFAULT_ARTICLE_PAGE_COUNT
    )
}