package com.example.routings.token

import com.example.dao.user.UserDao
import com.example.models.TokenInfo
import com.example.models.User
import com.example.models.responses.DataResponse
import com.example.plugins.security.JwtConfig
import com.example.plugins.security.noSession
import com.example.plugins.security.sessionUser
import com.example.plugins.security.verify
import com.example.util.empty
import com.example.util.logd
import com.example.util.refreshToken
import com.example.util.unknownErrorMsg
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureTokenRouting() {
    routing {
        get(refreshToken) {
            val token = call.request.headers["lunimary_token"] ?: empty
            val jwt = verify(token)
            if (jwt == null) {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = DataResponse<TokenInfo>().copy(
                        msg = "token expired."
                    )
                )
                return@get
            }
            val principal = call.principal<User>()
            "test principal=$principal".logd("token")
            if (call.noSession<TokenInfo>()) {
                "refresh noSession".logd("token")
                return@get
            }
            val user = UserDao.readByUsername(call.sessionUser!!.username)
            if (user == null) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = DataResponse<TokenInfo>().copy(msg = unknownErrorMsg)
                )
                return@get
            }
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<TokenInfo>().copy(
                    data = TokenInfo(
                        username = user.username,
                        accessToken = JwtConfig.generateAccessToken(user),
                        refreshToken = JwtConfig.generateRefreshToken(user)
                    ).also {
                        "refresh success:$it".logd("token")
                    }
                )
            )
        }
    }
}