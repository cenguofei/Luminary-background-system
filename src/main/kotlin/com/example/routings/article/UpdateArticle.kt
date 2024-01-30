package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.models.Article
import com.example.models.responses.DataResponse
import com.example.plugins.security.noSession
import com.example.util.invalidId
import com.example.util.noSuchArticle
import com.example.util.updateArticleByIdPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.updateArticle(articleDao: ArticleDao) {
    authenticate {
        put(updateArticleByIdPath) {
            if (call.noSession()) {
                return@put
            }
            if (call.invalidId()) {
                return@put
            }
            val newArticle = call.receive<Article>()
            articleDao.update(newArticle.id, newArticle)
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Unit>(
                    success = true
                )
            )
        }
    }
}