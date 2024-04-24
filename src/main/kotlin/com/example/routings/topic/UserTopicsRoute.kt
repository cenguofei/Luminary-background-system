package com.example.routings.topic

import com.example.models.Topic
import com.example.models.responses.DataResponse
import com.example.plugins.security.jwtUser
import com.example.util.userSelectedTopicsPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userTopics() {
    authenticate {
        get(userSelectedTopicsPath) {
            val user = call.jwtUser!!
            val topics = com.example.dao.topic.userTopics(user.id)
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<List<Topic>>().copy(
                    data = topics
                )
            )
        }
    }
}