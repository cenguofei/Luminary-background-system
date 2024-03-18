package com.example.routings.article.message

import com.example.util.messageCommentPath
import com.example.util.messageLikePath
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.messageForLike() {
    authenticate {
        get(messageLikePath) {

        }
    }
}