package com.example.routings.friend

import com.example.dao.friend.FriendDao
import com.example.dao.friend.FriendDaoImpl
import com.example.dao.user.UserDao
import com.example.util.friendRootPath
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureFriendRouting() {
    val friendDao: FriendDao = FriendDao
    val userDao = UserDao
    routing {
        route(friendRootPath) {
            follow(friendDao)
            unfollow(friendDao)
            myFollowings(friendDao, userDao)
            myFollowers(friendDao, userDao)
            existingFriendship(friendDao)
        }
    }
}