package com.example.dao.topic

import com.example.dao.LunimaryDao
import com.example.models.Topic
import com.example.models.tables.Topics

interface TopicDao : LunimaryDao<Topic, Topics> {
    override suspend fun create(data: Topic): Long

    override suspend fun delete(id: Long)

    override suspend fun read(id: Long): Topic?

    suspend fun read(ids: List<Long>): List<Topic>

    suspend fun readByTopic(topic: String): Topic?

    override suspend fun allData(): List<Topic>

    companion object : TopicDao by TopicDaoImpl()
}

suspend fun userTopics(userId: Long): List<Topic> {
    val topicsId = UserTopicsDao.read(userId)?.topics
    return TopicDao.read(topicsId?.toList() ?: emptyList())
}