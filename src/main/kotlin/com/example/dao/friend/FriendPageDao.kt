package com.example.dao.friend

import com.example.dao.LunimaryPage
import com.example.models.ext.UserFriend

class FriendPageDao(
    private val userId: Long
) : LunimaryPage<UserFriend> {
    override suspend fun pages(pageStart: Int, perPageCount: Int): List<UserFriend> {
        check()
        return friends.page(pageStart, perPageCount)
    }

    override suspend fun pageCount(): Long {
        check()
        return friends.size.toLong()
    }

    private suspend fun check() {
        if (_friends == null) {
            getData()
        }
    }

    private var _friends: List<UserFriend>? = null
    private val friends get() = _friends!!

    private suspend fun getData() {
        val mutualFollowUsers = FriendDao.mutualFollowUsers(userId)
            .distinctBy { it.user.id }
        _friends = mutualFollowUsers
    }
}