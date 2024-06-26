package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.models.responses.DataResponse
import com.example.plugins.security.sessionUser
import com.example.util.badRequest
import com.example.util.deleteArticleByIdPath
import com.example.util.noSessionAndInvalidId
import com.example.util.noSuchArticle
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.deleteArticleByIdRoute(articleDao: ArticleDao) {
    authenticate {
        delete(deleteArticleByIdPath) {
            if (call.noSessionAndInvalidId<Unit>()) {
                return@delete
            }

            val id = call.parameters["id"]?.toLong()!!
            val queryArticle = articleDao.read(id)
            if (call.noSuchArticle<Unit>(queryArticle)) {
                return@delete
            }

            if (call.badRequest<Unit> { queryArticle?.username != call.sessionUser?.username }) {
                return@delete
            }

            articleDao.delete(id)
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Unit>().copy(
                    msg = "The Article has deleted."
                )
            )
        }
    }
}