package com.example.dao.view.duration

import com.example.models.ViewDuration
import com.example.models.tables.ViewDurations
import com.example.plugins.database.database
import com.example.util.Default
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ViewDurationDaoImpl : ViewDurationDao {
    init {
        transaction(database) { SchemaUtils.create(ViewDurations) }
    }

    override suspend fun create(data: ViewDuration): Long {
        return dbTransaction {
            val exist = exist(data)
            if (exist.first) {
                update(exist.second!!.id, data.copy(duration = data.duration + exist.second!!.duration))
                exist.second!!.id
            } else {
                ViewDurations.insert { state ->
                    state[duration] = data.duration
                    state[timestamp] = if (data.duration == Long.Default) System.currentTimeMillis() else data.duration
                    state[type] = data.type
                    state[userId] = data.userId
                }[ViewDurations.id]
            }
        }
    }

    override suspend fun update(id: Long, data: ViewDuration) {
        dbTransaction {
            ViewDurations.update(
                where = {
                    ViewDurations.id eq data.id
                },
                body = { state ->
                    state[duration] = data.duration
                    state[timestamp] = data.timestamp
                    state[type] = data.type
                    state[userId] = data.userId
                }
            )
        }
    }

    private suspend fun exist(data: ViewDuration): Pair<Boolean, ViewDuration?> {
        return dbTransaction {
            val result = ViewDurations.selectAll().where {
                (ViewDurations.userId eq data.userId) and (ViewDurations.type eq data.type)
            }.limit(1).mapToViewDuration()
            result.isNotEmpty() to if (result.isNotEmpty()) result[0] else null
        }
    }

    override suspend fun userDurationsTop10(loginUserId: Long): List<ViewDuration> {
        return dbTransaction {
            ViewDurations.selectAll().where { ViewDurations.userId eq loginUserId }
                .orderBy(ViewDurations.duration, order = SortOrder.DESC)
                .limit(10)
                .mapToViewDuration()
        }
    }

    private fun Iterable<ResultRow>.mapToViewDuration(): List<ViewDuration> {
        return map {
            ViewDuration(
                id = it[ViewDurations.id],
                userId = it[ViewDurations.userId],
                timestamp = it[ViewDurations.timestamp],
                type = it[ViewDurations.type],
                duration = it[ViewDurations.duration],
            )
        }
    }
}