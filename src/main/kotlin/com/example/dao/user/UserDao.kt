package com.example.dao.user

import com.example.dao.LunimaryDao
import com.example.models.User
import com.example.models.tables.Users
import com.example.util.Default

interface UserDao : LunimaryDao<User, Users> {
    override suspend fun create(data: User): Long

    override suspend fun read(id: Long): User?

    suspend fun readByUsername(username: String, pwdNeeded: Boolean = false): User?

    override suspend fun update(id: Long, data: User)

    override suspend fun updateViaRead(id: Long, update: (old: User) -> User) {}

    suspend fun updateByUsername(username: String, user: User)

    override suspend fun delete(id: Long)

    suspend fun batchUsers(ids: List<Long>): List<User>

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<User>

    override suspend fun pageCount(): Long

    override suspend fun count(): Long = Users.count()

    companion object : UserDao by UserDaoFacadeImpl()
}

open class DefaultUserDao : UserDao {
    override suspend fun create(data: User): Long {
        return Long.Default
    }

    override suspend fun read(id: Long): User? {
        return null
    }

    override suspend fun readByUsername(username: String, pwdNeeded: Boolean): User? {
        return null
    }

    override suspend fun update(id: Long, data: User) {
    }

    override suspend fun updateByUsername(username: String, user: User) {
    }

    override suspend fun delete(id: Long) {
    }

    override suspend fun batchUsers(ids: List<Long>): List<User> {
        return emptyList()
    }

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<User> {
        return emptyList()
    }

    override suspend fun pageCount(): Long {
        return Long.Default
    }
}