package com.example.dao.friend

import com.example.dao.LunimaryPage
import com.example.models.ext.FollowInfo
import com.example.routings.user.logic.MyFollowings

class MyFollowingsPageDao(
    private val userId: Long
) : LunimaryPage<FollowInfo> {
    override suspend fun pages(pageStart: Int, perPageCount: Int): List<FollowInfo> {
        check()
        return myFollowings.page(pageStart, perPageCount)
    }

    override suspend fun pageCount(): Long {
        check()
        return myFollowings.size.toLong()
    }

    private suspend fun check() {
        if (_myFollowings == null) {
            getData()
        }
    }

    private var _myFollowings: List<FollowInfo>? = null
    private val myFollowings get() = _myFollowings!!

    private suspend fun getData() {
        val existing = FriendDao.existingOfUserId(userId)
        if (!existing) {
            _myFollowings = emptyList()
            return
        }
        val myFollowingsUser = MyFollowings.myFollowings(userId)
        val myFollowingsUsersId = myFollowingsUser.map { it.id }
        val myFollowingFollowedUser = FriendDao.userFollow(myFollowingsUsersId)
        _myFollowings = myFollowingsUser.map {
            val alsoFollowMe = myFollowingFollowedUser.find { friend ->
                friend.userId == it.id && friend.whoId == userId
            } != null
            FollowInfo(
                myFollow = it,
                alsoFollowMe = alsoFollowMe
            )
        }
    }
}