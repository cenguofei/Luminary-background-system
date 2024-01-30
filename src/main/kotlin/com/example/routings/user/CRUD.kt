package com.example.routings.user

import com.example.dao.user.UserDao
import com.example.models.User
import com.example.models.responses.UserResponse
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.plugins.security.sessionUser
import com.example.util.empty
import com.example.util.invalidId
import com.example.util.logi
import com.example.util.noSessionMsg
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

/**
 * 创建、删除、更新、读取用户
 */
fun Route.crud(userDao: UserDao) {
    authenticate {
        /**
         * 用户根据id获取“自己”的信息
         * 注意：需要session
         */
        get("/{id}") {
            val id = call.parameters["id"]?.toLong()
            if (call.checkSessionAndId(id, userDao)) {
                return@get
            }
            val user = userDao.readById(id!!)
            if (user != null) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = UserResponse(message = empty, success = true, data = user)
                )
            } else {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = UserResponse(message = "No user match id: $id")
                )
            }
        }
    }

    authenticate {
        // Update user
        put("/{id}") {
            val id = call.parameters["id"]?.toLong()
            if (call.checkSessionAndId(id, userDao)) {
                return@put
            }

            val user = call.receive<User>()

            if (user.username != call.sessionUser?.username) {
                val existingUser = userDao.readByUsername(user.username)
                if (existingUser != null) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = UserResponse(message = "This username is already in use.", success = false)
                    )
                    return@put
                } else {
                    //更新session
                    call.sessions.set(call.sessionUser?.copy(username = user.username))
                }
            }

            userDao.updateById(id!!, user)
            "Hello, ${call.jwtUser}!".logi("credentials")
            call.respond(
                status = HttpStatusCode.OK,
                message = UserResponse(message = empty, success = true)
            )
        }
    }

    authenticate {
        // Delete user
        delete("/{id}") {
            val id = call.parameters["id"]?.toLong()
            if (call.checkSessionAndId(id, userDao)) {
                return@delete
            }
            userDao.delete(id!!)
            "Hello, ${call.jwtUser}!".logi("credentials")
            call.respond(
                status = HttpStatusCode.OK,
                message = UserResponse(success = true)
            )
        }
    }
}

private suspend fun ApplicationCall.checkSessionAndId(
    id: Long?,
    userDao: UserDao
) : Boolean {
    var flag = false
    //没有登录状态，应该先登录
    if (noSession) {
        respond(
            status = HttpStatusCode.Conflict,
            message = UserResponse(message = noSessionMsg, shouldLogin = true)
        )
        flag = true
    }

    if (id == null) {
        respond(
            status = HttpStatusCode.Conflict,
            message = UserResponse(message = invalidId)
        )
        flag = true
    }

    //测试环境下容易弄混用户之间的session，做这个检查防止测试时不知道用错了session
    if (id != null) {
        val queryUser = userDao.readById(id)
        val sessionUser = sessionUser
        if (sessionUser?.username != queryUser?.username) {
            respond(
                status = HttpStatusCode.Conflict,
                message = UserResponse(
                    message = "You can only get or modify your own information."
                )
            )
            flag = true
        }
    }

    return flag
}