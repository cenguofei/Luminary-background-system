package com.example.dao.user

import com.example.models.User
import com.example.plugins.database.userLongCache
import com.example.plugins.database.userStringCache
import com.example.util.logd

class UserDaoFacadeImpl(private val delegate: UserDao = UserDaoImpl()) : UserDao {
    private val longCache = userLongCache
    private val stringCache = userStringCache

    override suspend fun create(data: User): Long =
        delegate.create(data)
            .also { longCache.put(data.id, data) }

    override suspend fun read(id: Long): User? =
        longCache[id]?.let { cacheUser ->
            "readById from cache: user=$cacheUser".logd()
            cacheUser
        } ?: delegate.read(id).also { u ->
            u?.let { longCache.put(id, it) }
        }

    override suspend fun readByUsername(username: String, pwdNeeded: Boolean): User? =
        stringCache[username]?.let { cacheUser ->
            "readByUsername from cache: user=$cacheUser".logd()
            cacheUser
        } ?: delegate.readByUsername(username, pwdNeeded).also { u ->
            u?.let { stringCache.put(username, it) }
        }

    override suspend fun update(id: Long, data: User) =
        delegate.update(id, data).also { longCache.put(id, data) }

    override suspend fun updateByUsername(username: String, user: User) =
        delegate.updateByUsername(username, user)
            .also { stringCache.put(username, user) }

    override suspend fun delete(id: Long) =
        delegate.delete(id).also { longCache.remove(id) }

    override suspend fun updateViaRead(id: Long, update: (old: User) -> User) {
        delegate.updateViaRead(id, update)
    }
}