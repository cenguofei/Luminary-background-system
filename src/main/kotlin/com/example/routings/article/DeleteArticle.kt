package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.models.Article
import com.example.models.responses.DataResponse
import com.example.plugins.security.noSession
import com.example.plugins.security.sessionUser
import com.example.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.deleteArticleById(articleDao: ArticleDao) {
    authenticate {
        delete(deleteArticleByIdPath) {
            if (call.noSessionAndInvalidId()) {
                return@delete
            }

            val id = call.parameters["id"]?.toLong()!!
            val queryArticle = articleDao.read(id)
            if (call.noSuchArticle(queryArticle)) {
                return@delete
            }

            if (call.badRequest { queryArticle?.username != call.sessionUser?.username }) {
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