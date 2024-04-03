package com.example.dao.friend

import com.example.dao.LunimaryPage
import com.example.models.ext.FollowersInfo
import com.example.routings.user.logic.MyFollowers
import com.example.util.logd

class MyFollowersPageDao(
    private val userId: Long
) : LunimaryPage<FollowersInfo> {
    override suspend fun pages(pageStart: Int, perPageCount: Int): List<FollowersInfo> {
        check()
        return myFollowers.page(pageStart, perPageCount)
    }

    override suspend fun pageCount(): Long {
        check()
        return myFollowers.size.toLong()
    }

    private suspend fun check() {
        if (_myFollowers == null) {
            getData()
        }
    }

    private var _myFollowers: List<FollowersInfo>? = null
    private val myFollowers get() = _myFollowers!!

    private suspend fun getData() {
        val existing = FriendDao.existingOfWhoId(userId)
        if (!existing) {
            _myFollowers = emptyList()
            return
        }
        val fans = MyFollowers.myFollowers(userId)
        "user $userId fans=${fans.map { it.id }}".logd("myFollowers")
        val myFollowings = FriendDao.myFollowings(userId)
        _myFollowers = fans.map {
            val alsoFollow = myFollowings.find { friend ->
                friend.whoId == it.id
            } != null
            FollowersInfo(
                follower = it,
                alsoFollow = alsoFollow
            )
        }
    }
}