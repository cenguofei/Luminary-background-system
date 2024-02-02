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
 * client�ڵ�¼֮ǰ�ȵ��ô˷�������Ƿ��¼
 * ����Ƿ��ǵ�¼״̬
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
 * ��¼
 * ��¼�ɹ���ͬʱ����session��token
 * ע�⣺��¼֮ǰӦ����ʹ��[checkIsLogin]����¼״̬
 */
fun Route.login(userDao: UserDao) {
    post("/login") {
        //��ȡ������
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
        //������ݿ���û�д��û�
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
            //��¼�ɹ�,����session
            call.sessions.set(UserSession(username, generateNonce()))
            val sendUser = queryUser.copy(password = empty)
            //����token��������֤
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
 * �˳���¼
 */
fun Route.logout() {
    post("/logout") {
        call.sessions.clear<UserSession>()
        call.respondText("Logout Success.")
    }
}