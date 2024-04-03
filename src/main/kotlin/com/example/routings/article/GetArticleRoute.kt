package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.models.Article
import com.example.models.responses.DataResponse
import com.example.util.getArticleByIdPath
import com.example.util.invalidId
import com.example.util.noSuchArticle
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getArticleById(articleDao: ArticleDao) {
    get(getArticleByIdPath) {
        if (call.invalidId<Article>()) {
            return@get
        }
        val id = call.parameters["id"]?.toLong()!!
        val queryArticle = articleDao.read(id)
        if (call.noSuchArticle<Article>(queryArticle)) {
            return@get
        }
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<Article>().copy(data = queryArticle)
        )
    }
}