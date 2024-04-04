package com.example.routings.article.like

import com.example.dao.like.LikeDao
import com.example.models.Like
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.plugins.security.noSession
import com.example.util.cancelLikePath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.cancelLikeRoute(likeDao: LikeDao) {
    authenticate {
        post(cancelLikePath) {
            if (call.noSession<Unit>()) {
                return@post
            }
            call.receive<Like> { like ->
                val exists = likeDao.exists(like)
                if (!exists) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = DataResponse<Unit>().copy(
                            msg = "The like relationship do not exists"
                        )
                    )
                }
                likeDao.delete(like)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>()
                )
            }
        }
    }
}