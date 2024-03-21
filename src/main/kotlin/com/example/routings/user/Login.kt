package com.example.routings.user

import com.example.dao.user.UserDao
import com.example.models.responses.UserData
import com.example.models.responses.UserResponse
import com.example.plugins.security.JwtConfig
import com.example.plugins.security.UserSession
import com.example.plugins.security.jwtUser
import com.example.routings.user.status.Status
import com.example.routings.user.status.StatusManager
import com.example.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*

/**
 * ��¼
 * ��¼�ɹ���ͬʱ����session��token
 * ע�⣺��¼֮ǰӦ����ʹ��[checkIsLogin]����¼״̬
 */
fun Route.login(userDao: UserDao) {
    post(loginPath) {
        //��ȡ������
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
        //������ݿ���û�д��û�
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
            //��¼�ɹ�,����session
            call.sessions.set(UserSession(username, generateNonce()))
            val sendUser = queryUser.copy(password = empty)
            //����token��������֤
            val token = JwtConfig.generateAccessToken(sendUser)
            call.response.header("access_token", token)
            call.response.header("refresh_token", JwtConfig.generateRefreshToken(sendUser.ofNoPassword()))

            val status = Status(isForeground = true, username = username)
            StatusManager.addOrUpdateStatus(status)

            call.respond(
                status = HttpStatusCode.OK,
                message = UserResponse().copy(
                    msg = "Login success.",
                    data = UserData(user = sendUser.copy(password = empty)),
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