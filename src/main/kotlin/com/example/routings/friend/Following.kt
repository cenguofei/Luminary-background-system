package com.example.routings.friend

import com.example.dao.friend.FriendDao
import com.example.dao.user.UserDao
import com.example.models.ext.FollowInfo
import com.example.models.ext.FollowersInfo
import com.example.models.responses.RelationData
import com.example.models.responses.RelationResponse
import com.example.util.invalidId
import com.example.util.myFollowersPath
import com.example.util.myFollowingsPath
import com.example.util.notNull
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * ÎÒµÄ¹Ø×¢
 */
fun Route.myFollowings(friendDao: FriendDao, userDao: UserDao) {
    get(myFollowingsPath) {
        if (call.invalidId<List<FollowInfo>>("userId")) {
            return@get
        }
        val userId = call.parameters["userId"].notNull.toLong()
        val onlyNum = call.request.queryParameters["onlyNum"]?.toBooleanStrictOrNull() ?: false
        val existing = friendDao.existingOfUserId(userId)
        if (!existing) {
            call.respond(
                status = HttpStatusCode.OK,
                message = RelationResponse<FollowInfo>().copy(
                    msg = "You haven't followed anyone yet."
                )
            )
            return@get
        }
        val myFollowings = friendDao.myFollowings(userId)
        val myFollowingsUsersId = myFollowings.map { it.whoId }
        val myFollowingsUser = userDao.batchUsers(myFollowingsUsersId)
        if (onlyNum) {
            call.respond(
                status = HttpStatusCode.OK,
                message = RelationResponse<FollowInfo>().copy(
                    data = RelationData(num = myFollowingsUser.size)
                )
            )
            return@get
        }
        val myFollowingFollowedUser = friendDao.userFollow(myFollowingsUsersId)
        call.respond(
            status = HttpStatusCode.OK,
            message = RelationResponse<FollowInfo>().copy(
                data = RelationData(
                    relations = myFollowingsUser.map {
                        val alsoFollowMe = myFollowingFollowedUser.find { friend ->
                            friend.userId == it.id && friend.whoId == userId
                        } != null
                        FollowInfo(
                            myFollow = it,
                            alsoFollowMe = alsoFollowMe
                        )
                    }
                )
            )
        )
    }
}

fun Route.myFollowers(friendDao: FriendDao, userDao: UserDao) {
    get(myFollowersPath) {
        if (call.invalidId<List<FollowersInfo>>("userId")) {
            return@get
        }
        val userId = call.parameters["userId"].notNull.toLong()
        val onlyNum = call.request.queryParameters["onlyNum"]?.toBooleanStrictOrNull() ?: false
        val existing = friendDao.existingOfWhoId(userId)
        if (!existing) {
            call.respond(
                status = HttpStatusCode.OK,
                message = RelationResponse<FollowersInfo>().copy(
                    msg = "You currently have no fans."
                )
            )
            return@get
        }
        val fansId = friendDao.allFollowMeOnlyFriends(userId).map { it.userId }
        val fans = userDao.batchUsers(fansId)
        if (onlyNum) {
            call.respond(
                status = HttpStatusCode.OK,
                message = RelationResponse<FollowersInfo>().copy(
                    data = RelationData(num = fans.size)
                )
            )
            return@get
        }

        val myFollowings = FriendDao.myFollowings(userId)
        call.respond(
            status = HttpStatusCode.OK,
            message = RelationResponse<FollowersInfo>().copy(
                data = RelationData(
                    relations = fans.map {
                        val alsoFollow = myFollowings.find { friend ->
                            friend.whoId == it.id
                        } != null
                        FollowersInfo(
                            follower = it,
                            alsoFollow = alsoFollow
                        )
                    }
                )
            )
        )
    }
}