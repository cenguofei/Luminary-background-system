package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.models.Article
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.plugins.security.noSession
import com.example.plugins.security.sessionUser
import com.example.util.badRequest
import com.example.util.updateArticleByIdPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.updateArticleRoute(articleDao: ArticleDao) {
    authenticate {
        put(updateArticleByIdPath) {
            if (call.noSession<Unit>()) {
                return@put
            }
            call.receive<Article> { newArticle ->
                if (call.badRequest<Unit> { newArticle.username != call.sessionUser?.username }) {
                    return@put
                }
                articleDao.update(newArticle.id, newArticle)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>().copy(msg = "Update success.")
                )
            }
        }
    }
}