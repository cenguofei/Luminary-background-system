package com.example.routings.user.routes

import com.example.models.responses.DataResponse
import com.example.plugins.security.UserSession
import com.example.plugins.security.sessionUser
import com.example.routings.user.status.StatusManager
import com.example.util.logd
import com.example.util.logoutPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*


/**
 * ÍË³öµÇÂ¼
 */
fun Route.logoutRoute() {
    authenticate {
        post(logoutPath) {
            try {
                val username = call.sessionUser?.username ?: run {
                    "sessionUser == null".logd()
                    return@post
                }
                StatusManager.removeStatus(username)

                call.sessions.clear<UserSession>()
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>().copy(msg = "Logout Success.")
                )
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>().copy(msg = e.message.toString())
                )
            }
        }
    }
}