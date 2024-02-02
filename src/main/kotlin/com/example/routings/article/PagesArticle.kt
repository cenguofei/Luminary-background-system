package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.models.responses.ArticleResponse
import com.example.models.responses.PAGE_COUNT
import com.example.util.Default
import com.example.util.badRequest
import com.example.util.isNull
import com.example.util.pagesArticlePath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * 分页获取文章列表
 * curPage: 从0开始
 */
fun Route.pagesArticle(articleDao: ArticleDao) {
    get(pagesArticlePath) {
        val queryParameters = call.request.queryParameters
        val curPage = queryParameters["curPage"]?.toInt()
        val perPageCount = queryParameters["perPageCount"]?.toInt()

        if (call.badRequest { curPage.isNull || perPageCount.isNull } ) {
            return@get
        }

        val count = articleDao.count()
        val articles = articleDao.pages(curPage!!, perPageCount = perPageCount!!)

        call.respond(
            status = HttpStatusCode.OK,
            message = ArticleResponse(
                curPage = curPage,
                pageSize = if (count % perPageCount != 0L) count / perPageCount + 1 else count / perPageCount,
                totalArticle = count,
                data = articles
            )
        )
    }
}