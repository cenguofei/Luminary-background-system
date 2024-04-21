package com.example.routings.topic

import com.example.dao.topic.UserTopicsDao
import com.example.models.UserSelectedTopics
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.plugins.security.jwtUser
import com.example.util.createOrUpdateTopicsPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createOrUpdateRoute() {
    authenticate {
        post(createOrUpdateTopicsPath) {
            call.receive<UserSelectedTopics> {
                val userId = call.jwtUser!!.id
                if (it.userId != userId) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = DataResponse<Unit>()
                    )
                    return@post
                }
                UserTopicsDao.createOrUpdate(
                    userId = userId,
                    topics = it.topics.toList()
                )
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>()
                )
            }
        }
    }
}