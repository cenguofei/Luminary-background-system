package com.example.routings.article.message

import com.example.util.messageFollowPath
import com.example.util.messageLikePath
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.messageForFollow() {
    authenticate {
        get(messageFollowPath) {

        }
    }
}