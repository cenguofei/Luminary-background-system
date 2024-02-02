package com.example.routings.user

import com.example.dao.user.UserDao
import com.example.models.User
import com.example.models.UserStatus
import com.example.models.responses.UserResponse
import com.example.plugins.receive
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.plugins.security.sessionUser
import com.example.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
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
            val user = userDao.read(id!!)
            if (user != null) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = UserResponse(msg = empty, success = true, data = user)
                )
            } else {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = UserResponse(msg = "No user match id: $id")
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

            call.receive<User>(
                errorMessage = UserResponse(msg = HttpStatusCode.BadRequest.description, success = false)
            ) { user ->
                if (user.username != call.sessionUser?.username) {
                    val existingUser = userDao.readByUsername(user.username)
                    if (existingUser != null) {
                        call.respond(
                            status = HttpStatusCode.Conflict,
                            message = UserResponse(msg = "This username is already in use.", success = false)
                        )
                        return@put
                    } else {
                        //更新session
                        call.sessions.set(call.sessionUser?.copy(username = user.username))
                    }
                }

                userDao.update(id!!, user)
                "Hello, ${call.jwtUser}!".logi("credentials")
                call.respond(
                    status = HttpStatusCode.OK,
                    message = UserResponse(msg = empty, success = true)
                )
            }
        }
    }

    authenticate {
        // Delete user
        delete("/{id}") {
            val id = call.parameters["id"]?.toLong()
            if (call.checkSessionAndId(id, userDao)) {
                return@delete
            }
            userDao.updateViaRead(id!!) { old ->
                old.copy(status = UserStatus.Deleted)
            }
            "Hello, ${call.jwtUser}!".logi("credentials")
            call.respond(
                status = HttpStatusCode.OK,
                message = UserResponse(
                    msg = "Your account has been cancelled, " +
                            "and the relevant data will be cleared in 15 days. " +
                            "Please do not log in to your account during this period, " +
                            "otherwise the cancellation will be cancelled.",
                    success = true
                )
            )
        }
    }

    //TODO delete this test block
    authenticate {
        // Delete user
        delete("/direct_delete/{id}") {
            val id = call.parameters["id"]?.toLong()
            if (call.checkSessionAndId(id, userDao)) {
                return@delete
            }
            userDao.delete(id!!)
            "Hello, ${call.jwtUser}!".logi("credentials")
            call.respond(
                status = HttpStatusCode.OK,
                message = UserResponse(
                    msg = "Delete success.",
                    success = true
                )
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
            message = UserResponse(msg = noSessionMsg, shouldLogin = true)
        )
        flag = true
    }

    if (id == null) {
        respond(
            status = HttpStatusCode.Conflict,
            message = UserResponse(msg = invalidId)
        )
        flag = true
    }

    //测试环境下容易弄混用户之间的session，做这个检查防止测试时不知道用错了session
    if (id != null) {
        val queryUser = userDao.read(id)
        val sessionUser = sessionUser
        if (sessionUser?.username != queryUser?.username) {
            respond(
                status = HttpStatusCode.Conflict,
                message = UserResponse(
                    msg = "You can only get or modify your own information."
                )
            )
            flag = true
        }
    }

    return flag
}