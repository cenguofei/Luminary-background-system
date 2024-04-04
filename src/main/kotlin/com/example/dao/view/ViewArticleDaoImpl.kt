package com.example.dao.view

import com.example.models.ViewArticle
import com.example.models.tables.ViewArticles
import com.example.plugins.database.database
import com.example.util.Default
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ViewArticleDaoImpl : ViewArticleDao {
    init { transaction(database) { SchemaUtils.create(ViewArticles) } }

    override suspend fun create(data: ViewArticle): Long {
        if (exist(data)) return -1
        return dbTransaction {
            ViewArticles.insert { state ->
                state[userId] = data.userId
                state[articleId] = data.articleId
                val ts = if (data.timestamp == Long.Default) System.currentTimeMillis() else data.timestamp
                state[timestamp] = ts
            }[ViewArticles.id]
        }
    }

    override suspend fun exist(viewArticle: ViewArticle): Boolean {
        return dbTransaction {
            ViewArticles.selectAll().where {
                (ViewArticles.userId eq viewArticle.userId) and (ViewArticles.articleId eq viewArticle.articleId)
            }.limit(1).mapToViewArticle().singleOrNull() != null
        }
    }

    override suspend fun friendsViewArticles(friends: List<Long>): List<ViewArticle> {
        return dbTransaction {
            ViewArticles.selectAll().where {
                ViewArticles.userId inList friends
            }.mapToViewArticle()
        }
    }

    private fun Iterable<ResultRow>.mapToViewArticle(): List<ViewArticle> {
        return map {
            ViewArticle(
                id = it[ViewArticles.id],
                userId = it[ViewArticles.userId],
                articleId = it[ViewArticles.articleId],
                timestamp = it[ViewArticles.timestamp]
            )
        }
    }
}