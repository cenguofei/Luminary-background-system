package com.example.routings.user

import com.auth0.jwt.exceptions.JWTVerificationException
import com.example.models.responses.DataResponse
import com.example.plugins.security.JwtConfig
import com.example.plugins.security.sessionUser
import com.example.plugins.security.verify
import com.example.util.checkIsLoginPath
import com.example.util.empty
import com.example.util.logd
import com.example.util.logi
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * client�ڵ�¼֮ǰ�ȵ��ô˷�������Ƿ��¼
 * ����Ƿ��ǵ�¼״̬
 */
fun Route.checkIsLogin() {
    post(checkIsLoginPath) {
        val token = call.request.headers["lunimary_token"] ?: empty
        val jwt = verify(token)
        "jwt��$jwt, token=$token".logd("auth_jwt")
        if (jwt == null) {
            "loginInfo=${
                LoginInfo(
                    isLogin = false,
                    tokenExpired = true
                )
            }".logd("auth_jwt")

            call.respond(
                status = HttpStatusCode.Conflict, // ���ܷ���Un auth
                message = DataResponse<LoginInfo>().copy(
                    msg = "token expired.",
                    data = LoginInfo(
                        isLogin = false,
                        tokenExpired = true
                    )
                ),
            )
            return@post
        }

        val session = call.sessionUser
        "checkIsLogin session is : ${session.toString()}".logi()
        if (session == null) {
            call.respond(
                HttpStatusCode.Conflict,
                DataResponse<LoginInfo>().copy(
                    msg = "No Login.",
                    data = LoginInfo(
                        isLogin = false,
                        tokenExpired = false
                    )
                )
            )
            "loginInfo=${
                LoginInfo(
                    isLogin = false,
                    tokenExpired = false
                )
            }".logd("auth_jwt")
        } else {
            call.respond(
                HttpStatusCode.OK,
                DataResponse<LoginInfo>().copy(
                    msg = "Already login.",
                    data = LoginInfo(
                        isLogin = true,
                        tokenExpired = false
                    )
                )
            )
            "loginInfo=${
                LoginInfo(
                    isLogin = true,
                    tokenExpired = false
                )
            }".logd("auth_jwt")
        }
    }
}

@kotlinx.serialization.Serializable
data class LoginInfo(
    val isLogin: Boolean,
    val tokenExpired: Boolean
)