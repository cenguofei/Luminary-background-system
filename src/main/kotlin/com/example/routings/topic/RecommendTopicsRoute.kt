package com.example.routings.topic

import com.example.dao.topic.TopicDao
import com.example.models.Topic
import com.example.models.responses.DataResponse
import com.example.util.recommendTopicsPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.recommendTopics() {
    get(recommendTopicsPath) {
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<List<Topic>>().copy(
                data = TopicDao.allData()
            )
        )
    }
}