package com.example.routings.article.collect

import com.example.dao.article.CollectDao
import com.example.models.Collect
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.createCollectPath
import com.example.util.internalErrorMsg
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createCollect(collectDao: CollectDao) {
    authenticate {
        post(createCollectPath) {
            if (call.noSession()) {
                return@post
            }
            call.receive<Collect> {
                if (it.collectUserId != call.jwtUser?.id) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = DataResponse<Unit>().copy(msg = internalErrorMsg)
                    )
                    return@post
                }
                if (collectDao.exists(it)) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = DataResponse<Unit>().copy(msg = "this relation already existing!")
                    )
                    return@post
                }
                collectDao.create(it)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>()
                )
            }
        }
    }
}