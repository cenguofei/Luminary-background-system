package com.example.dao.article

import com.example.models.Collect
import com.example.models.tables.Collects
import com.example.models.tables.DELETED_ARTICLE_ID
import com.example.plugins.database.database
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class CollectDaoImpl : CollectDao {
    init {
        transaction(database) { SchemaUtils.create(Collects) }
    }

    private val articleDao = ArticleDao

    override suspend fun create(data: Collect): Long = dbTransaction {
        articleDao.updateViaRead(data.articleId) { it.copy(collections = it.collections + 1) }
        Collects.insert { state ->
            state[collectUserId] = data.collectUserId
            state[articleId] = data.articleId
            state[timestamp] = data.timestamp
        }[Collects.id]
    }

    override suspend fun delete(id: Long) {
        dbTransaction {
            read(id)?.let { collect ->
                articleDao.updateViaRead(collect.articleId) { it.copy(collections = it.collections - 1) }
            }
            Collects.deleteWhere { Collects.id eq id }
        }
    }

    override suspend fun delete(collect: Collect) {
        dbTransaction {
            Collects.deleteWhere {
                (collectUserId eq collect.collectUserId) and (articleId eq collect.articleId)
            }
        }
    }

    override suspend fun read(id: Long): Collect? {
        return dbTransaction {
            Collects.selectAll().where { Collects.id eq id }
                .mapToCollect().singleOrNull()
        }
    }

    override suspend fun getAllCollectsOfUser(userId: Long): List<Collect> = dbTransaction {
        Collects.selectAll().where {
            Collects.collectUserId eq userId
        }.map {
            Collect(
                id = it[Collects.id],
                collectUserId = it[Collects.collectUserId],
                articleId = it[Collects.articleId] ?: DELETED_ARTICLE_ID,
                timestamp = it[Collects.timestamp]
            )
        }
    }

    override suspend fun getAllCollectsOfArticle(articleId: Long): List<Collect> = dbTransaction {
        Collects.selectAll().where {
            Collects.articleId eq articleId
        }.map {
            Collect(
                id = it[Collects.id],
                collectUserId = it[Collects.collectUserId],
                articleId = it[Collects.articleId] ?: DELETED_ARTICLE_ID,
                timestamp = it[Collects.timestamp]
            )
        }
    }

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Collect> =
        Collects.getPageQuery(pageStart, perPageCount).mapToCollect()

    override suspend fun exists(collect: Collect): Boolean {
        return dbTransaction {
            Collects.selectAll().where {
                (Collects.collectUserId eq collect.collectUserId) and (Collects.articleId eq collect.articleId)
            }.limit(1).firstOrNull() != null
        }
    }

    private fun Iterable<ResultRow>.mapToCollect(): List<Collect> {
        return map {
            Collect(
                id = it[Collects.id],
                collectUserId = it[Collects.collectUserId],
                articleId = it[Collects.articleId] ?: DELETED_ARTICLE_ID,
                timestamp = it[Collects.timestamp]
            )
        }
    }
}