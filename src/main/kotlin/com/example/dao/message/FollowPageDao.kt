package com.example.dao.message

import com.example.dao.LunimaryPage
import com.example.dao.friend.FriendDao
import com.example.models.ext.UserFriend

class FollowPageDao(
    private val loginUserId: Long
) : LunimaryPage<UserFriend> {
    override suspend fun pages(pageStart: Int, perPageCount: Int): List<UserFriend> {
        getData()
        return data.page(pageStart, perPageCount)
    }

    override suspend fun pageCount(): Long {
        return data.size.toLong()
    }

    private suspend fun getData() {
        if (_data == null) {
            val query = FriendDao.allFollowMeToUsers(loginUserId)
            _data = query.filter { it.visibleToOwner }
        }
    }

    private var _data: List<UserFriend>? = null
    val data get() = _data!!
}