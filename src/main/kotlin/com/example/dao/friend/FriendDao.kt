package com.example.dao.friend

import com.example.dao.LuminaryDao
import com.example.models.Friend
import com.example.models.User
import com.example.models.ext.UserFriend
import com.example.models.tables.Friends

interface FriendDao : LuminaryDao<Friend, Friends> {
    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Friend>

    override suspend fun create(data: Friend): Long

    override suspend fun delete(id: Long)

    suspend fun delete(userId: Long, whoId: Long)

    override suspend fun read(id: Long): Friend?

    /**
     * 我的关注
     * @param id 我的id
     */
    suspend fun myFollowings(id: Long) : List<Friend>

    /**
     * 关注我的
     * @param id 我的id
     */
    suspend fun allFollowMeOnlyFriends(id: Long) : List<Friend>

    suspend fun allFollowMeToUsers(loginUserId: Long) : List<UserFriend>

    /**
     * 取关
     * @param userId
     * @param who 取关的人
     */
    suspend fun unfollow(userId: Long, who: Long)

    /**
     * 删除[Friend.userId]和[Friend.whoId]都为null的数据
     */
    suspend fun deleteBothNull()

    suspend fun existing(userId: Long, whoId: Long): Boolean

    suspend fun existingOfUserId(userId: Long): Boolean

    suspend fun existingOfWhoId(whoId: Long): Boolean

    override suspend fun count(): Long = Friends.count()

    companion object : FriendDao by FriendDaoImpl()
}