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
 * client�ڵ�¼֮ǰ�ȵ��ô˷�������Ƿ��¼
 * ����Ƿ��ǵ�¼״̬
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
 * �˳���¼
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