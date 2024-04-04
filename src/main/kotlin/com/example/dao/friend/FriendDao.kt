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
     * ����ids��ע���û�
     * @param ids ��ע���û���id
     */
    suspend fun userFollow(ids: List<Long>): List<Friend>

    /**
     * �ҵĹ�ע
     * @param loginUserId �ҵ�id
     */
    suspend fun myFollowings(loginUserId: Long) : List<Friend>

    /**
     * ��ע�ҵ�
     * @param loginUserId �ҵ�id
     */
    suspend fun allFollowMeOnlyFriends(loginUserId: Long) : List<Friend>

    /**
     * ��ע�ҵ��û�
     */
    suspend fun allFollowMeToUsers(loginUserId: Long) : List<UserFriend>

    /**
     * ��ȡ�ҵ����ѣ��������໥��ע��
     */
    suspend fun mutualFollowUsers(loginUserId: Long) : List<UserFriend>

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

    companion object : FriendDao by FriendDaoImpl()
}