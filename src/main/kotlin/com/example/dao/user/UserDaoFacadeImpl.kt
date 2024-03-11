package com.example.dao.user

import com.example.models.User
import com.example.plugins.database.userTypeOfIdCache
import com.example.plugins.database.userTypeOfUsernameCache
import com.example.util.logd

class UserDaoFacadeImpl(private val delegate: UserDao = UserDaoImpl()) : UserDao {
    private val idCache = userTypeOfIdCache
    private val usernameCache = userTypeOfUsernameCache

    override suspend fun create(data: User): Long =
        delegate.create(data)
            .also {
                idCache.put(data.id, data)
                usernameCache.put(data.username, data)
            }

    override suspend fun read(id: Long): User? =
        idCache[id]?.let { cacheUser ->
            "readById from cache: user=$cacheUser".logd()
            cacheUser
        } ?: delegate.read(id).also { u ->
            u?.let {
                idCache.put(id, it)
                usernameCache.put(u.username, u)
            }
        }

    override suspend fun readByUsername(username: String, pwdNeeded: Boolean): User? =
        usernameCache[username]?.let { cacheUser ->
            "readByUsername from cache: user=$cacheUser".logd()
            cacheUser
        } ?: delegate.readByUsername(username, pwdNeeded).also { u ->
            u?.let {
                usernameCache.put(username, it)
                idCache.put(it.id, it)
            }
        }

    override suspend fun update(id: Long, data: User) =
        delegate.update(id, data).also {
            idCache.put(id, data)
            usernameCache.put(data.username, data)
        }

    override suspend fun updateByUsername(username: String, user: User) =
        delegate.updateByUsername(username, user)
            .also {
                usernameCache.put(username, user)
                idCache.put(user.id, user)
            }

    override suspend fun delete(id: Long) =
        delegate.delete(id).also {
            idCache.remove(id)
            val findUser = usernameCache.find { it.value.id == id }?.value
            if (findUser != null) {
                usernameCache.remove(findUser.username)
            }
        }

    override suspend fun updateViaRead(id: Long, update: (old: User) -> User) {
        delegate.updateViaRead(id, update)
        val newUser = delegate.read(id)
        if (newUser != null) {
            idCache.put(newUser.id, newUser)
            usernameCache.put(newUser.username, newUser)
        }
    }

    override suspend fun batchUsers(ids: List<Long>): List<User> {
        return delegate.batchUsers(ids)
    }

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<User> {
        return delegate.pages(pageStart, perPageCount)
    }

    override suspend fun existing(id: Long): Boolean {
        return delegate.existing(id)
    }
}