package com.example.plugins

import com.example.routings.file.configureFileRouting
import com.example.routings.user.configureUserRouting
import io.ktor.server.application.*

fun Application.configureRouting() {
    configureUserRouting()
    configureFileRouting()
}
