package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.models.PublishState
import com.example.models.responses.DataResponse
import com.example.plugins.security.jwtUser
import com.example.util.approveArticlePath
import com.example.util.notNull
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.approveArticleRoute() {
    authenticate {
        get(approveArticlePath) {
            val articleId = call.parameters["articleId"]?.toLongOrNull()
            val publishState = call.request.queryParameters["state"]?.let {
                PublishState.valueOf(it)
            }
            if (articleId == null || publishState == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = DataResponse<Unit>().copy(
                        msg = "The ID of article or audit result cannot be null."
                    )
                )
                return@get
            }
            val dbArticle = ArticleDao.read(articleId)
            if (dbArticle == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = DataResponse<Unit>().copy(
                        msg = "The article with ID:$articleId not exist."
                    )
                )
                return@get
            }

            if (dbArticle.publishState == PublishState.Approved) {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = DataResponse<Unit>().copy(
                        msg = "The article has been reviewed."
                    )
                )
                return@get
            }
            if (publishState == PublishState.Auditing) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = DataResponse<Unit>().copy(
                        msg = "The audit result cannot be auditing."
                    )
                )
                return@get
            }

            ArticleDao.audit(
                oldArticle = dbArticle,
                newArticle = dbArticle.copy(publishState = publishState),
                auditor = call.jwtUser.notNull
            )
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Unit>().copy(
                    msg = "Audit success, the article current state is ${publishState.name}."
                )
            )
        }
    }
}