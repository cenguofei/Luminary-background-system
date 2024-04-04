package com.example.dao.friend

import com.example.dao.LunimaryDao
import com.example.models.Friend
import com.example.models.ext.UserFriend
import com.example.models.tables.Friends

interface FriendDao : LunimaryDao<Friend, Friends> {
    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Friend>

    override suspend fun pageCount(): Long

    override suspend fun create(data: Friend): Long

    override suspend fun delete(id: Long)

    suspend fun delete(userId: Long, whoId: Long)

    override suspend fun read(id: Long): Friend?

    /**
     * 返回ids关注的用户
     * @param ids 关注的用户的id
     */
    suspend fun userFollow(ids: List<Long>): List<Friend>

    /**
     * 我的关注
     * @param loginUserId 我的id
     */
    suspend fun myFollowings(loginUserId: Long) : List<Friend>

    /**
     * 关注我的
     * @param loginUserId 我的id
     */
    suspend fun allFollowMeOnlyFriends(loginUserId: Long) : List<Friend>

    /**
     * 关注我的用户
     */
    suspend fun allFollowMeToUsers(loginUserId: Long) : List<UserFriend>

    /**
     * 获取我的朋友，朋友是相互关注的
     */
    suspend fun mutualFollowUsers(loginUserId: Long) : List<UserFriend>

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