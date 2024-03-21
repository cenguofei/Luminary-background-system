package com.example.routings.ws

import io.ktor.server.websocket.*

data class SocketSession(
    val session: DefaultWebSocketServerSession,
    val username: String,
    val sessionId: String
)


