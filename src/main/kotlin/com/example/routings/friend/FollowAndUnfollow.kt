package com.example.routings.friend

import com.example.dao.friend.FriendDao
import com.example.dao.user.UserDaoFacadeImpl
import com.example.models.Friend
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * ¹Ø×¢
 */
fun Route.follow(friendDao: FriendDao) {
    authenticate {
        post(followPath) {
            call.receive<Friend> {
                if (call.noSession()) {
                    return@post
                }
                if (call.checkInvalid(it, friendDao)) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = DataResponse<Unit>().copy(msg = internalErrorMsg)
                    )
                    return@post
                }
                friendDao.create(it)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>()
                )
            }
        }
    }
}

private suspend fun ApplicationCall.checkInvalid(friend: Friend, friendDao: FriendDao): Boolean {
    return jwtUser?.id != friend.userId || friend.userId == friend.whoId
            || UserDaoFacadeImpl().read(friend.whoId) == null
            || friendDao.existing(friend.userId, friend.whoId)
}

/**
 * È¡¹Ø
 */
fun Route.unfollow(friendDao: FriendDao) {
    authenticate {
        post(unfollowPath) {
            if (call.noSession()) {
                return@post
            }
            if (call.invalidId("whoId")) {
                return@post
            }
            val whoId = call.parameters["whoId"].notNull.toLong()
            if (call.jwtUser?.id == whoId) {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = DataResponse<Unit>().copy(msg = internalErrorMsg)
                )
                return@post
            }
            friendDao.delete(call.jwtUser.notNull.id, whoId)
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Unit>()
            )
        }
    }
}
