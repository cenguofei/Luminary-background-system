package com.example.routings.article.message

import com.example.dao.friend.FriendDao
import com.example.models.User
import com.example.models.ext.UserFriend
import com.example.models.responses.DataResponse
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.messageFollowPath
import com.example.util.unknownErrorMsg
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.messageForFollow() {
    authenticate {
        get(messageFollowPath) {
            if (call.noSession<LikeMessageResponse>()) {
                return@get
            }
            // 1. jwt.jwtUser ÄÃµ½µÇÂ¼ÓÃ»§ id
            val loginUserId = call.jwtUser?.id ?: run {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = DataResponse<LikeMessageResponse>().copy(msg = unknownErrorMsg)
                )
                return@get
            }
            val friendDao = FriendDao
            val allFollowMeToUsers = friendDao.allFollowMeToUsers(loginUserId)
            call.respond(
                status = HttpStatusCode.OK,
                message =DataResponse<List<UserFriend>>().copy(
                    data = allFollowMeToUsers
                )
            )
        }
    }
}