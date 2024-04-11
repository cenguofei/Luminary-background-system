package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.models.responses.DataResponse
import com.example.util.empty
import com.example.util.isArticleDeletedPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.existingArticle() {
    get(isArticleDeletedPath) {
        val articleId = call.parameters["articleId"]?.toLongOrNull()
        if (articleId == null) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = DataResponse<Boolean>().copy(
                    msg = "The article ID is null."
                )
            )
            return@get
        }
        val existing = ArticleDao.existing(articleId)
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<Boolean>().copy(
                data = existing,
                msg = if(!existing) "The article has deleted." else empty
            )
        )
    }
}