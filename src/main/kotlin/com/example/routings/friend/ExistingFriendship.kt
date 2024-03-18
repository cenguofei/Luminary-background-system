package com.example.routings.friend

import com.example.dao.friend.FriendDao
import com.example.models.Friend
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.util.existingFriendshipPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.existingFriendship(friendDao: FriendDao) {
    post(existingFriendshipPath) {
        call.receive<Friend> {
            val existing = friendDao.existing(it.userId, it.whoId)
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<ExistingFriendship>().copy(
                    data = ExistingFriendship(existing)
                )
            )
        }
    }
}

@kotlinx.serialization.Serializable
data class ExistingFriendship(
    val exists: Boolean = false
)