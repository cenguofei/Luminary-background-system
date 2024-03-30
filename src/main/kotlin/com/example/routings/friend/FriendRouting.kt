package com.example.routings.friend

import com.example.dao.friend.FriendDao
import com.example.dao.friend.FriendPageDao
import com.example.dao.friend.MyFollowersPageDao
import com.example.dao.friend.MyFollowingsPageDao
import com.example.dao.user.UserDao
import com.example.models.ext.FollowInfo
import com.example.models.ext.FollowersInfo
import com.example.models.ext.UserFriend
import com.example.models.responses.PageOptions
import com.example.models.responses.pagesData
import com.example.util.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureFriendRouting() {
    val friendDao: FriendDao = FriendDao
    val userDao = UserDao
    routing {
        route(friendRootPath) {
            follow(friendDao)
            unfollow(friendDao)
            existingFriendship(friendDao)
            //onlyNum
            myFollowings(friendDao, userDao)
            myFollowers(friendDao, userDao)
            mutualFollowFriends()
            //entity
            pageMyFollowings()
            pageFollowers()
            pageMutualFollowFriends()
        }
    }
}

fun Route.pageMutualFollowFriends() {
    pagesData<UserFriend>(
        requestPath = pageMyFriendsPath,
        pageOptions = PageOptions(
            onIntercept = { it.invalidIdNoRespond("userId") },
            onCreateDao = { FriendPageDao(it.id("userId")) }
        )
    )
}

fun Route.pageFollowers() {
    pagesData<FollowersInfo>(
        requestPath = pageMyFollowersPath,
        pageOptions = PageOptions(
            onIntercept = { it.invalidIdNoRespond("userId") },
            onCreateDao = { MyFollowersPageDao(it.id("userId")) }
        )
    )
}

fun Route.pageMyFollowings() {
    pagesData<FollowInfo>(
        requestPath = pageMyFollowingsPath,
        pageOptions = PageOptions(
            onIntercept = { it.invalidIdNoRespond("userId") },
            onCreateDao = { MyFollowingsPageDao(it.id("userId")) }
        )
    )
}
