package com.example.routings.user.routes

import com.example.dao.user.UserDao
import com.example.models.User
import com.example.models.responses.UserData
import com.example.models.responses.UserResponse
import com.example.routings.user.logic.verifyInput
import com.example.util.encrypt
import com.example.util.registerPath
import com.example.util.withLogi
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerRoute(userDao: UserDao) {
    post(registerPath) {
        val parameters = call.receiveParameters()
        val username = parameters["username"]
        val password = parameters["password"]

        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            call.respond(
                status = HttpStatusCode.Conflict,
                message = UserResponse().copy(
                    msg =  "Username or password cannot be empty.".withLogi()
                )
            )
            return@post
        }

        val queryUser = userDao.readByUsername(username)
        if (queryUser != null) {
            call.respond(
                status = HttpStatusCode.Conflict,
                message = UserResponse().copy(
                    msg =  "The username already exists. Please register a different username.".withLogi(),
                )
            )
            return@post
        }

        //ะฃั้
        val verify = verifyInput(username, password)
        if (!verify.first) {
            call.respond(
                status = HttpStatusCode.Conflict,
                message = UserResponse().copy(
                    msg = verify.second
                )
            )
            return@post
        }

        val newUser = User(username = username, password = encrypt(password))
        userDao.create(newUser)

        call.respond(
            status = HttpStatusCode.OK,
            message = UserResponse().copy(
                msg = "Register success.",
                data = UserData(user = newUser.ofNoPassword())
            )
        )
    }
}