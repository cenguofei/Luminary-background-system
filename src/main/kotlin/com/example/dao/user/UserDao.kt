package com.example.dao.user

import com.example.dao.LuminaryDao
import com.example.models.User
import com.example.models.tables.Users

interface UserDao : LuminaryDao<User, Users> {
    override suspend fun create(data: User): Long

    override suspend fun read(id: Long): User?

    suspend fun readByUsername(username: String, pwdNeeded: Boolean = false): User?

    override suspend fun update(id: Long, data: User)

    override suspend fun updateViaRead(id: Long, update: (old: User) -> User) { }

    suspend fun updateByUsername(username: String, user: User)

    override suspend fun delete(id: Long)

    suspend fun batchUsers(ids: List<Long>) : List<User>

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<User>

    override suspend fun count(): Long = Users.count()

    companion object : UserDao by UserDaoFacadeImpl()
}