package com.example.routings.user.friend

import com.example.dao.friend.FriendDao
import com.example.models.responses.DataResponse
import com.example.plugins.security.jwtUser
import com.example.util.invisibleFollowPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.invisibleFollow() {
    authenticate {
        get(invisibleFollowPath) {
            val followerID = call.parameters["followerId"]?.toLongOrNull()
            if (followerID == null) {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = DataResponse<Boolean>().copy(
                        msg = "No follower ID."
                    )
                )
                return@get
            }

            val user = call.jwtUser!!
            val loginUserId = user.id

            val readFriend = FriendDao.read(userId = followerID, whoId = loginUserId)
            if (readFriend == null) {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = DataResponse<Boolean>().copy(
                        msg = "The relation between user ID $loginUserId and $followerID not exist."
                    )
                )
                return@get
            }

            if (!readFriend.visibleToOwner) {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = DataResponse<Boolean>().copy(
                        msg = "Duplicate set to invisible."
                    )
                )
                return@get
            }
            FriendDao.update(readFriend.copy(visibleToOwner = false))
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Boolean>()
            )
        }
    }
}