package com.example.dao.topic

import com.example.dao.LunimaryDao
import com.example.models.UserSelectedTopics
import com.example.models.tables.UserTopics

interface UserTopicsDao : LunimaryDao<UserSelectedTopics, UserTopics> {
    suspend fun createOrUpdate(userId: Long, topics: List<Long>)

    override suspend fun create(data: UserSelectedTopics): Long

    override suspend fun read(id: Long): UserSelectedTopics?

    companion object : UserTopicsDao by UserTopicsDaoImpl()
}