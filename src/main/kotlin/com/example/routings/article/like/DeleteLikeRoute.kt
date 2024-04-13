package com.example.routings.article.like

import com.example.dao.article.ArticleDao
import com.example.dao.like.LikeDao
import com.example.models.responses.DataResponse
import com.example.plugins.security.jwtUser
import com.example.util.deleteLikePath
import com.example.util.id
import com.example.util.invalidId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.deleteLike() {
    authenticate {
        delete(deleteLikePath) {
            if (call.invalidId<Boolean>()) {
                return@delete
            }
            val likeID = call.id
            val readLike = LikeDao.read(likeID)
            if (readLike == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = DataResponse<Boolean>().copy(
                        msg = "The like ID : $likeID not exist."
                    )
                )
                return@delete
            }
            val user = call.jwtUser!!
            val userArticlesOnlyId = ArticleDao.userArticlesOnlyId(user.id)
            if (readLike.articleId !in userArticlesOnlyId) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = DataResponse<Boolean>().copy(
                        msg = "The article about the like not exist."
                    )
                )
                return@delete
            }
            if (!readLike.visibleToOwner) {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = DataResponse<Boolean>().copy(
                        msg = "Duplicate set."
                    )
                )
                return@delete
            }
            LikeDao.update(likeID, readLike.copy(visibleToOwner = false))
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Boolean>().copy(
                    data = true
                )
            )
        }
    }
}
