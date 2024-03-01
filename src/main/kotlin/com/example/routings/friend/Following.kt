package com.example.routings.friend

import com.example.dao.friend.FriendDao
import com.example.dao.user.UserDao
import com.example.models.User
import com.example.models.responses.DataResponse
import com.example.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * ÎÒµÄ¹Ø×¢
 */
fun Route.myFollowings(friendDao: FriendDao, userDao: UserDao) {
    get(myFollowingsPath) {
        if (call.invalidId("userId")) {
            return@get
        }
        val userId = call.parameters["userId"].notNull.toLong()
        val existing = friendDao.existingOfUserId(userId)
        if (!existing) {
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<List<User>>().copy(
                    msg = "You haven't followed anyone yet.",
                    data = emptyList()
                )
            )
            return@get
        }
        val myFollowingsId = friendDao.myFollowings(userId)
            .map { it.whoId }
        val myFollowings = userDao.batchUsers(myFollowingsId)
        if (myFollowings.isEmpty()) {
            call.respond(
                status = HttpStatusCode.Conflict,
                message = DataResponse<List<User>>().copy(msg = internalErrorMsg)
            )
            return@get
        }
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<List<User>>().copy(data = myFollowings)
        )
    }
}

fun Route.myFollowers(friendDao: FriendDao, userDao: UserDao) {
    get(myFollowersPath) {
        if (call.invalidId("userId")) {
            return@get
        }
        val userId = call.parameters["userId"].notNull.toLong()
        val existing = friendDao.existingOfWhoId(userId)
        if (!existing) {
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<List<User>>().copy(
                    msg = "You currently have no fans.",
                    data = emptyList()
                )
            )
            return@get
        }
        val fansId = friendDao.allFollowMe(userId).map { it.userId }
        val fans = userDao.batchUsers(fansId)
        if (fans.isEmpty()) {
            call.respond(
                status = HttpStatusCode.Conflict,
                message = DataResponse<List<User>>().copy(msg = internalErrorMsg)
            )
            return@get
        }
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<List<User>>().copy(data = fans)
        )
    }
}