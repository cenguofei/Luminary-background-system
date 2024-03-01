package com.example.routings.user.status

import com.example.models.responses.DataResponse
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.isForegroundStr
import com.example.util.onlineStatusPath
import com.example.util.refreshToken
import com.example.util.unknownErrorMsg
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureReportOnlineStatusRouting() {
    routing {
        reportOnlineStatus()
    }
}

fun Route.reportOnlineStatus() {
    authenticate {
        post(onlineStatusPath) {
            if (call.noSession()) {
                return@post
            }
            val parameters = call.receiveParameters()
            val foreground = parameters[isForegroundStr].toBoolean()
            val username = call.jwtUser?.username ?: run {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = DataResponse<Unit>().copy(msg = unknownErrorMsg)
                )
                return@post
            }
            val status = Status(isForeground = foreground, username = username)
            StatusManager.addOrUpdateStatus(status)
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Unit>().copy(msg = HttpStatusCode.OK.description)
            )
        }
    }
}