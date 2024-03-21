package com.example.routings.file

import com.example.dao.user.UserDao
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureFileRouting() {
    val userDao = UserDao
    routing {
        route("/file") {
            download()
            upload(userDao)
        }
    }
}