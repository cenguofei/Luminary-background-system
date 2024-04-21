package com.example.dao.topic

import com.example.models.Topic
import com.example.models.tables.Topics
import com.example.plugins.database.database
import com.example.util.Default
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class TopicDaoImpl : TopicDao {
    init {
        transaction(database) { SchemaUtils.create(Topics) }
    }

    override suspend fun create(data: Topic): Long = dbTransaction {
        Topics.insert { state ->
            state[topic] = data.topic
            val timestamp = if (data.timestamp == Long.Default) {
                System.currentTimeMillis()
            } else data.timestamp
            state[Topics.timestamp] = timestamp
        }[Topics.id]
    }

    override suspend fun delete(id: Long) {
        dbTransaction {
            Topics.deleteWhere { Topics.id eq id }
        }
    }

    override suspend fun read(id: Long): Topic? {
        return dbTransaction {
            Topics.selectAll()
                .where { Topics.id eq id }
                .limit(1)
                .mapToTopic()
                .singleOrNull()
        }
    }

    override suspend fun readByTopic(topic: String): Topic? {
        return dbTransaction {
            Topics.selectAll()
                .where { Topics.topic eq topic }
                .limit(1)
                .mapToTopic()
                .singleOrNull()
        }
    }

    override suspend fun read(ids: List<Long>): List<Topic> {
        return dbTransaction {
            val selected = Topics.selectAll().where { Topics.id inList ids }
                .mapToTopic()
            mutableListOf<Topic>().apply {
                ids.forEach {
                    this += selected.find { s -> s.id == it }!!
                }
            }
        }
    }

    override suspend fun allData(): List<Topic> {
        return dbTransaction {
            Topics.selectAll()
                .orderBy(Topics.fashion, SortOrder.DESC)
                .mapToTopic()
        }
    }
}


fun Iterable<ResultRow>.mapToTopic(): List<Topic> {
    return map {
        Topic(
            id = it[Topics.id],
            topic = it[Topics.topic],
            timestamp = it[Topics.timestamp],
            fashion = it[Topics.fashion]
        )
    }
}