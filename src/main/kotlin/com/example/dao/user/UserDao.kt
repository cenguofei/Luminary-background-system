package com.example.dao.user

import com.example.models.User

interface UserDao {
    suspend fun create(user: User): Long

    suspend fun readById(id: Long): User?

    suspend fun readByUsername(username: String, pwdNeeded: Boolean = false): User?

    suspend fun updateById(id: Long, user: User)

    suspend fun updateByUsername(username: String, user: User)

    suspend fun delete(id: Long)
}