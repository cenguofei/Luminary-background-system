package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.models.Article
import com.example.models.responses.DataResponse
import com.example.util.deleteArticleByIdPath
import com.example.util.invalidId
import com.example.util.noSuchArticle
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.deleteArticleById(articleDao: ArticleDao) {
    authenticate {
        delete(deleteArticleByIdPath) {
            if (call.invalidId()) {
                return@delete
            }
            val id = call.parameters["id"]?.toLong()!!
            val queryArticle = articleDao.read(id)
            if (call.noSuchArticle(queryArticle)) {
                return@delete
            }

            articleDao.delete(id)
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Unit>(
                    success = true
                )
            )
        }
    }
}