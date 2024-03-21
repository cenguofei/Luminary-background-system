package com.example.routings.user

import com.example.dao.user.UserDao
import com.example.models.User
import com.example.models.UserStatus
import com.example.models.responses.UserData
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
         * 用户根据id获取用户的信息
         * 注意：需要session
         */
        get(getUserPath) {
            val id = call.parameters["userId"]?.toLong()
            if (call.checkSessionAndId(id, userDao)) {
                return@get
            }
            val user = userDao.read(id!!)
            if (user != null) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = UserResponse().copy(msg = empty, data = UserData(user = user.ofNoPassword()))
                )
            } else {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = UserResponse().copy(msg = "No user match id: $id")
                )
            }
        }
    }

    authenticate {
        // Update user
        put(updateUserPath) {
            val id = call.parameters["id"]?.toLong()
            if (call.checkSessionAndId(id, userDao)) {
                return@put
            }

            call.receive<User>(
                errorMessage = UserResponse().copy(msg = HttpStatusCode.BadRequest.description)
            ) { user ->
                if (user.username != call.sessionUser?.username) {
                    val existingUser = userDao.readByUsername(user.username)
                    if (existingUser != null) {
                        call.respond(
                            status = HttpStatusCode.Conflict,
                            message = UserResponse().copy(msg = "This username is already in use.")
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
                    message = UserResponse().copy(msg = empty)
                )
            }
        }
    }

    authenticate {
        // Delete user
        delete(deleteUserPath) {
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
                message = UserResponse().copy(
                    msg = "Your account has been cancelled, " +
                            "and the relevant data will be cleared in 15 days. " +
                            "Please do not log in to your account during this period, " +
                            "otherwise the cancellation will be cancelled.",
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
                message = UserResponse().copy(msg = "Delete success.")
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
            message = UserResponse().copy(
                msg = noSessionMsg,
                data = UserData(shouldLogin = true)
            )
        )
        flag = true
    }

    if (id == null) {
        respond(
            status = HttpStatusCode.Conflict,
            message = UserResponse().copy(msg = invalidId)
        )
        flag = true
    }

    return flag
}