package com.example.dao.friend

import com.example.dao.LuminaryDao
import com.example.models.Friend
import com.example.models.tables.Friends
import com.example.models.tables.Likes

interface FriendDao : LuminaryDao<Friend, Friends> {
    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Friend>

    override suspend fun create(data: Friend): Long

    override suspend fun delete(id: Long)

    suspend fun delete(userId: Long, whoId: Long)

    override suspend fun read(id: Long): Friend?

    /**
     * �ҵĹ�ע
     * @param id �ҵ�id
     */
    suspend fun myFollowings(id: Long) : List<Friend>

    /**
     * ��ע�ҵ�
     * @param id �ҵ�id
     */
    suspend fun allFollowMe(id: Long) : List<Friend>

    /**
     * ȡ��
     * @param userId
     * @param who ȡ�ص���
     */
    suspend fun unfollow(userId: Long, who: Long)

    /**
     * ɾ��[Friend.userId]��[Friend.whoId]��Ϊnull������
     */
    suspend fun deleteBothNull()

    suspend fun existing(userId: Long, whoId: Long): Boolean

    suspend fun existingOfUserId(userId: Long): Boolean

    suspend fun existingOfWhoId(whoId: Long): Boolean

    override suspend fun count(): Long = Friends.count()
}