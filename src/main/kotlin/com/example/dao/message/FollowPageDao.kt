package com.example.dao.message

import com.example.dao.LunimaryPage
import com.example.dao.friend.FriendDao
import com.example.models.ext.UserFriend

class FollowPageDao(
    private val loginUserId: Long
) : LunimaryPage<UserFriend> {
    override suspend fun pages(pageStart: Int, perPageCount: Int): List<UserFriend> {
        return FriendDao.allFollowMeToUsers(loginUserId)
            .page(pageStart, perPageCount)
    }

    override suspend fun pageCount(): Long {
        return FriendDao.allFollowMeToUsers(loginUserId).count().toLong()
    }
}