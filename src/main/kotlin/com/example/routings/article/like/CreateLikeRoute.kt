package com.example.routings.article.like

import com.example.dao.like.LikeDao
import com.example.models.Like
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.createLikePath
import com.example.util.internalErrorMsg
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.giveALikeRoute(likeDao: LikeDao) {
    authenticate {
        post(createLikePath) {
            if (call.noSession<Unit>()) {
                return@post
            }
            call.receive<Like> {
                if (it.userId != call.jwtUser?.id) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = DataResponse<Unit>().copy(msg = internalErrorMsg)
                    )
                    return@post
                }
                if (likeDao.exists(it)) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = DataResponse<Unit>().copy(msg = "this relation already existing!")
                    )
                    return@post
                }
                likeDao.create(it)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>()
                )
            }
        }
    }
}