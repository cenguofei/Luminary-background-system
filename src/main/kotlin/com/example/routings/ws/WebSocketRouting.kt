package com.example.routings.ws

import com.example.plugins.security.sessionUser
import com.example.util.wsRootPath
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.consumeEach

fun Application.configureWebSocketRouting() {
    routing {
        route(wsRootPath) {
            webSocket {
                println("onConnect")
                if (call.sessionUser == null) {
                    close(
                        CloseReason(code = CloseReason.Codes.VIOLATED_POLICY, message = "ws no session.")
                    )
                    return@webSocket
                }
                val session = call.sessionUser!!
                MessageSessionManager.set(
                    key = session.username,
                    value = SocketSession(
                        session = this,
                        username = session.username,
                        sessionId = session.sessionId
                    )
                )
                try {
                    incoming.consumeEach {

                    }
                } catch (e: ClosedReceiveChannelException) {
                    println("onClose ${closeReason.await()}")
                } catch (e: Throwable) {
                    println("onError ${closeReason.await()}")
                    e.printStackTrace()
                }
            }
        }

    }
}