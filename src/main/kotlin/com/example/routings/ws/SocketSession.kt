package com.example.routings.ws

import com.example.models.User
import io.ktor.server.websocket.*

data class SocketSession(
    val session: DefaultWebSocketServerSession,
    val username: String,
    val sessionId: String
)


