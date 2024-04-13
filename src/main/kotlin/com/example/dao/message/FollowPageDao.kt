package com.example.dao.message

import com.example.dao.LunimaryPage
import com.example.dao.friend.FriendDao
import com.example.models.ext.ClientUserFriend
import com.example.models.ext.RemoteUserFriend

class FollowPageDao(
    private val loginUserId: Long
) : LunimaryPage<ClientUserFriend> {
    override suspend fun pages(pageStart: Int, perPageCount: Int): List<ClientUserFriend> {
        getData()
        return data.page(pageStart, perPageCount)
    }

    override suspend fun pageCount(): Long {
        return data.size.toLong()
    }

    private suspend fun getData() {
        if (_data == null) {
            val query = FriendDao.allFollowMeToUsers(loginUserId)
            _data = query.filter { it.visibleToOwner }.map {
                ClientUserFriend(
                    user = it.user,
                    beFriendTime = it.beFriendTime,
                )
            }
        }
    }

    private var _data: List<ClientUserFriend>? = null
    val data get() = _data!!
}