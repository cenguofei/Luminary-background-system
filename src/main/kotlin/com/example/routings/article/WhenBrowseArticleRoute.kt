package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.models.responses.DataResponse
import com.example.util.whenBrowseArticlePath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.whenBrowseArticleRoute(articleDao: ArticleDao) {
    authenticate {
        get(whenBrowseArticlePath) {
            var isSuccess = false
            val articleId = call.parameters["articleId"]?.toLongOrNull() ?: run {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = DataResponse<Boolean>().copy(
                        msg = "The article id cannot be null or empty.",
                        data = isSuccess
                    )
                )
                return@get
            }
            articleDao.updateViaRead(articleId) {
                isSuccess = true
                it.copy(viewsNum = it.viewsNum + 1)
            }
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Boolean>().copy(
                    data = isSuccess
                )
            )
        }
    }
}