package com.example.routings.article.comment

import com.example.dao.article.CommentDao
import com.example.models.Comment
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.createCommentPath
import com.example.util.internalErrorMsg
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createCommentRoute(commentDao: CommentDao) {
    authenticate {
        post(createCommentPath) {
            if (call.noSession<Unit>()) {
                return@post
            }
            call.receive<Comment> {
                if (it.userId != call.jwtUser?.id) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = DataResponse<Unit>().copy(msg = internalErrorMsg)
                    )
                    return@post
                }
                commentDao.create(it)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>()
                )
            }
        }
    }
}