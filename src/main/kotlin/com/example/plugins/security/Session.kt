package com.example.plugins.security

import com.example.models.responses.DataResponse
import com.example.util.noSessionMsg
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import java.io.File

data class UserSession(val username: String, val sessionId: String)

fun Application.configureSession() {
    //会话，保存登录状态
    install(Sessions) {
        val secretSignKey = hex("a5c8f43f56ab97b91200af555bf1")
        val secretEncryptKey = hex("84941abf94a4492aba64af5103bc649f")

        //Session存储在内存中，当用户没有logout或者服务器没有停止运行，Session就存在
        header<UserSession>(
            name = "LUMINARY_SESSION",
            storage = directorySessionStorage(File("build/.sessions"))
        ) {
            //SessionTransportTransformerEncrypt uses the AES and HmacSHA256 algorithms
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }
}

/**
 * 登录后再次发送请求可获取到user
 */
val ApplicationCall.sessionUser get() = sessions.get<UserSession>()

val ApplicationCall.hasSession : Boolean get() = sessionUser != null

val ApplicationCall.noSession : Boolean get() = !hasSession

suspend fun ApplicationCall.noSession() : Boolean {
    if (noSession) {
        respond(
            status = HttpStatusCode.Conflict,
            DataResponse<Unit>(msg = noSessionMsg, success = false)
        )
        return true
    }
    return false
}