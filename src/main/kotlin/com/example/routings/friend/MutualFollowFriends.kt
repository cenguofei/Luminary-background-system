package com.example.routings.friend

import com.example.dao.friend.FriendDao
import com.example.dao.user.UserDao
import com.example.models.User
import com.example.models.ext.UserFriend
import com.example.models.responses.DataResponse
import com.example.models.responses.RelationData
import com.example.models.responses.RelationResponse
import com.example.models.tables.Users
import com.example.util.invalidId
import com.example.util.mutualFollowFriendsPath
import com.example.util.notNull
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.mutualFollowFriends() {
    get(mutualFollowFriendsPath) {
        if (call.invalidId<List<User>>("userId")) {
            return@get
        }
        val loginUserId = call.parameters["userId"].notNull.toLong()
        val onlyNum = call.request.queryParameters["onlyNum"]?.toBooleanStrictOrNull() ?: false
        val mutualFollowUsers = FriendDao.mutualFollowUsers(loginUserId)
            .distinctBy { it.user.id }
        if (mutualFollowUsers.isEmpty()) {
            call.respond(
                status = HttpStatusCode.OK,
                message = RelationResponse<UserFriend>().copy(
                    msg = "You don't have any friends yet."
                )
            )
            return@get
        }
        if (onlyNum) {
            call.respond(
                status = HttpStatusCode.OK,
                message = RelationResponse<List<UserFriend>>().copy(
                    data = RelationData(num = mutualFollowUsers.size)
                )
            )
        } else {
            call.respond(
                status = HttpStatusCode.OK,
                message = RelationResponse<UserFriend>().copy(
                    data = RelationData(relations = mutualFollowUsers)
                )
            )
        }
    }
}