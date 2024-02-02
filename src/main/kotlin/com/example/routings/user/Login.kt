package com.example.routings.user

import com.example.dao.user.UserDao
import com.example.util.encrypt
import com.example.models.responses.IsLoginResponse
import com.example.models.responses.UserResponse
import com.example.plugins.security.JwtConfig
import com.example.plugins.security.UserSession
import com.example.plugins.security.jwtUser
import com.example.plugins.security.sessionUser
import com.example.util.empty
import com.example.util.logi
import com.example.util.withLogi
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*

/**
 * client在登录之前先调用此方法检查是否登录
 * 检查是否是登录状态
 */
fun Route.checkIsLogin() {
    post("/isLogin") {
        val session = call.sessionUser
        "checkIsLogin session is : ${session.toString()}".logi()
        if (session == null) {
            call.respond(HttpStatusCode.OK, IsLoginResponse(message = "No Login.", isLogin = false))
        } else {
            call.respond(HttpStatusCode.OK, IsLoginResponse(message = "Already login.", isLogin = true))
        }
    }
}

/**
 * 登录
 * 登录成功则同时设置session和token
 * 注意：登录之前应该先使用[checkIsLogin]检查登录状态
 */
fun Route.login(userDao: UserDao) {
    post("/login") {
        //读取表单数据
        val formParameters = call.receiveParameters()
        val username = formParameters["username"]
        val password = formParameters["password"]
        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            call.respond(
                message = UserResponse(
                    msg = "Username or password cannot be empty.".withLogi(),
                    success = false
                ),
                status = HttpStatusCode.Conflict
            )
            return@post
        }

        val queryUser = userDao.readByUsername(username, true)
        //如果数据库中没有此用户
        if (queryUser == null) {
            call.respond(
                message = UserResponse(
                    msg = "There is no such user, please register first.".withLogi(),
                    success = false
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
            val token = JwtConfig.generateToken(sendUser)
            call.response.header("token", token)

            call.respond(
                status = HttpStatusCode.OK,
                message = UserResponse(
                    msg = "Login success.",
                    data = sendUser,
                    success = true
                )
            )
            "Login success.".logi()
        } else {
            call.respond(
                message = UserResponse(
                    msg = "Password error.".withLogi(),
                    success = false
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
    post("/logout") {
        call.sessions.clear<UserSession>()
        call.respondText("Logout Success.")
    }
}