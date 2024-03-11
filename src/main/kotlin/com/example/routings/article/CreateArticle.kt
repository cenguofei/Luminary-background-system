package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.models.Article
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.createArticlePath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createArticle(articleDao: ArticleDao) {
    authenticate {
        post(createArticlePath) {
            if (call.noSession()) {
                return@post
            }
            call.receive<Article> {
                if (call.jwtUser?.id != it.userId) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = DataResponse<Unit>().copy(msg = HttpStatusCode.BadRequest.description)
                    )
                    return@post
                }

                val id = articleDao.create(it)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>().copy(msg = "Successfully created article with id $id")
                )
            }
        }
    }
}