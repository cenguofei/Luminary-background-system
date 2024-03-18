package com.example.routings.ws

import java.util.concurrent.ConcurrentHashMap

object MessageSessionManager {
    private val sessions = ConcurrentHashMap<String, SocketSession>()

    fun set(key: String, value: SocketSession) {
        if (sessions.containsKey(key)) {
            sessions.remove(key)
        }
        sessions[key] = value
    }

    fun get(key: String): SocketSession? {
        if (sessions.containsKey(key)) {
            return sessions[key]
        }
        return null
    }
}