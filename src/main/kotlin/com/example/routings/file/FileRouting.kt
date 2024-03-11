package com.example.routings.file

import com.example.dao.user.UserDao
import com.example.dao.user.UserDaoFacadeImpl
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureFileRouting() {
    val userDao: UserDao = UserDao.Default
    routing {
        route("/file") {
            download()
            upload(userDao)
        }
    }
}