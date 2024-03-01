package com.example.routings.user

import com.example.dao.user.UserDao
import com.example.models.responses.DataResponse
import com.example.models.responses.UserData
import com.example.models.responses.UserResponse
import com.example.plugins.security.*
import com.example.routings.user.status.Status
import com.example.routings.user.status.StatusManager
import com.example.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import org.h2.engine.User

/**
 * client在登录之前先调用此方法检查是否登录
 * 检查是否是登录状态
 */
fun Route.checkIsLogin() {
    post(checkIsLoginPath) {
        val session = call.sessionUser
        "checkIsLogin session is : ${session.toString()}".logi()
        if (session == null) {
            call.respond(
                HttpStatusCode.OK,
                DataResponse<Boolean>().copy(msg = "No Login.", data = false)
            )
        } else {
            call.respond(
                HttpStatusCode.OK,
                DataResponse<Boolean>().copy(msg = "Already login.", data = true)
            )
        }
    }
}

/**
 * 登录
 * 登录成功则同时设置session和token
 * 注意：登录之前应该先使用[checkIsLogin]检查登录状态
 */
fun Route.login(userDao: UserDao) {
    post(loginPath) {
        //读取表单数据
        val formParameters = call.receiveParameters()
        val username = formParameters["username"]
        val password = formParameters["password"]
        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            call.respond(
                message = UserResponse().copy(
                    msg = "Username or password cannot be empty.".withLogi()
                ),
                status = HttpStatusCode.Conflict
            )
            return@post
        }

        val queryUser = userDao.readByUsername(username, true)
        //如果数据库中没有此用户
        if (queryUser == null) {
            call.respond(
                message = UserResponse().copy(
                    msg = "There is no such user, please register first.".withLogi()
                ),
                status = HttpStatusCode.Conflict
            )
            return@post
        }
        if (encrypt(password) == queryUser.password) {
            "Hello, ${call.jwtUser}!".logi("credentials")
            //登录成功,设置session
            call.sessions.set(UserSession(username, generateNonce()))
            val sendUser = queryUser.copy(password = empty)
            //设置token，用于认证
            val token = JwtConfig.generateAccessToken(sendUser)
            call.response.header("access_token", token)
            call.response.header("refresh_token", JwtConfig.generateRefreshToken(sendUser))

            val status = Status(isForeground = true, username = username)
            StatusManager.addOrUpdateStatus(status)

            call.respond(
                status = HttpStatusCode.OK,
                message = UserResponse().copy(
                    msg = "Login success.",
                    data = UserData(user = sendUser),
                )
            )
            "Login success.".logi()
        } else {
            call.respond(
                message = UserResponse().copy(
                    msg = "Password error.".withLogi()
                ),
                status = HttpStatusCode.Conflict
            )
        }
    }
}

/**
 * 退出登录
 */
fun Route.logout() {
    authenticate {
        post(logoutPath) {
            try {
                val username = call.sessionUser?.username ?: run {
                    "sessionUser == null".logd()
                    return@post
                }
                StatusManager.removeStatus(username)

                call.sessions.clear<UserSession>()
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>().copy(msg = "Logout Success.")
                )
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>().copy(msg = e.message.toString())
                )
            }
        }
    }
}