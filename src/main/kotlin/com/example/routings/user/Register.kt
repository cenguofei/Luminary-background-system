package com.example.routings.user

import com.example.dao.user.UserDao
import com.example.util.encrypt
import com.example.models.User
import com.example.models.responses.UserResponse
import com.example.util.empty
import com.example.util.withLogi
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.register(userDao: UserDao) {
    post("/register") {
        val parameters = call.receiveParameters()
        val username = parameters["username"]
        val password = parameters["password"]

        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            call.respond(
                status = HttpStatusCode.Conflict,
                message = UserResponse(
                    message =  "Username or password cannot be empty.".withLogi(),
                    success = false
                )
            )
            return@post
        }

        val queryUser = userDao.readByUsername(username)
        if (queryUser != null) {
            call.respond(
                status = HttpStatusCode.Conflict,
                message = UserResponse(
                    message =  "The username already exists. Please register a different username.".withLogi(),
                    success = false
                )
            )
            return@post
        }

        val newUser = User(username = username, password = encrypt(password))
        userDao.create(newUser)

        call.respond(
            status = HttpStatusCode.OK,
            message = UserResponse(
                message = "Register success.",
                success = true,
                data = newUser.copy(password = empty)
            )
        )
    }
}