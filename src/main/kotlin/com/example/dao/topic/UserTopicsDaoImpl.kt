package com.example.dao.topic

import com.example.models.UserSelectedTopics
import com.example.models.tables.UserTopics
import com.example.plugins.database.database
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class UserTopicsDaoImpl : UserTopicsDao {
    init { transaction(database) { SchemaUtils.create(UserTopics) } }

    override suspend fun createOrUpdate(userId: Long, topics: List<Long>) {
        if (existUser(userId)) {
            //update
            update(userId, topics)
        } else {
            //create
            create(UserSelectedTopics(userId = userId, topics = topics.toTypedArray()))
        }
    }

    override suspend fun create(data: UserSelectedTopics): Long = dbTransaction {
        UserTopics.insert { state ->
            state[userId] = data.userId
            state[topics] = data.topics
        }[UserTopics.userId]
    }

    override suspend fun read(id: Long): UserSelectedTopics? {
        return dbTransaction {
            UserTopics.selectAll().where {
                UserTopics.userId eq id
            }.limit(1).map {
                UserSelectedTopics(
                    userId = it[UserTopics.userId],
                    topics = it[UserTopics.topics]
                )
            }.singleOrNull()
        }
    }

    private suspend fun update(userId: Long, newTopics: List<Long>) {
        val old = read(userId) ?: return
        val oldTopics = old.topics
        if (oldTopics.size == newTopics.size && oldTopics.all { it in newTopics }) {
            return
        }
        dbTransaction {
            UserTopics.update(
                where = {
                    UserTopics.userId eq userId
                },
                limit = 1
            ) { state ->
                state[topics] = newTopics.toTypedArray()
            }
        }
    }

    private suspend fun existUser(userId: Long): Boolean {
        return dbTransaction {
            UserTopics.selectAll().where {
                UserTopics.userId eq userId
            }.singleOrNull() != null
        }
    }
}