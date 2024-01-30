package com.example.routings.user

import com.example.dao.user.UserDao
import com.example.dao.user.UserDaoFacadeImpl
import com.example.routings.file.upload
import io.ktor.server.application.*
import io.ktor.server.routing.*

/**
 * TODO ÉÏ´«Í·Ïñ
 */
fun Application.configureUserRouting() {
    routing {
        route("/users") {
            val userDao: UserDao = UserDaoFacadeImpl()
            crud(userDao)
            checkIsLogin()
            login(userDao)
            logout()
            register(userDao)
        }
    }
}