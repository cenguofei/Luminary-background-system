package com.example.routings.token

import com.example.models.TokenInfo
import com.example.models.responses.DataResponse
import com.example.plugins.security.JwtConfig
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
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
        authenticate(optional = true) {
            get(refreshToken) {
                "refresh token: user=${call.jwtUser}"
                val user = call.jwtUser
                if (user == null) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = DataResponse<Unit>().copy(msg = unknownErrorMsg)
                    )
                    "refresh user=null".logd("token")
                    return@get
                }
                if (call.noSession()) {
                    "refresh noSession".logd("token")
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
}