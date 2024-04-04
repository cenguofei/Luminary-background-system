package com.example.routings.article.collect

import com.example.dao.collect.CollectDao
import com.example.models.Collect
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.plugins.security.noSession
import com.example.util.cancelCollectPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.cancelCollectRoute(collectDao: CollectDao) {
    authenticate {
        post(cancelCollectPath) {
            if (call.noSession<Unit>()) {
                return@post
            }
            call.receive<Collect> { like ->
                val exists = collectDao.exists(like)
                if (!exists) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = DataResponse<Unit>().copy(
                            msg = "The like relationship do not exists"
                        )
                    )
                }
                collectDao.delete(like)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>()
                )
            }
        }
    }
}