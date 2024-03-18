package com.example.routings.article.message

import com.example.util.messageRootPath
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureMessageRouting() {
    routing {
        route(messageRootPath) {
            messageForComments()
            messageForLike()
        }
    }
}