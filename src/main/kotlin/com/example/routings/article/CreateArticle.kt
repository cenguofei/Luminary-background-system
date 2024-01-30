package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.models.Article
import com.example.models.responses.DataResponse
import com.example.models.responses.Response
import com.example.plugins.security.noSession
import com.example.util.createArticlePath
import com.example.util.noSessionMsg
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createArticle(articleDao: ArticleDao) {
    authenticate {
        post(createArticlePath) {
            if (call.noSession()) {
                return@post
            }
            val article = call.receive<Article>()
            val id = articleDao.create(article)
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Unit>(
                    message = "Successfully created article with id $id",
                    success = true
                )
            )
        }
    }
}