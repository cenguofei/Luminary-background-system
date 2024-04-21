package com.example.routings.topic

import com.example.util.topicRootPath
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureTopicRouting() {
    routing {
        route(topicRootPath) {
            userTopics()
            createOrUpdateRoute()
            recommendTopics()
        }
    }
}