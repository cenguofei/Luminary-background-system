package com.example.dao.article

import com.example.models.Like
import com.example.models.tables.Articles
import com.example.models.tables.DELETED_ARTICLE_ID
import com.example.models.tables.Likes
import com.example.plugins.database.database
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class LikeDaoImpl : LikeDao {
    init { transaction(database) { SchemaUtils.create(Likes) } }

    private val articleDao: ArticleDao = ArticleDaoImpl()

    override suspend fun create(data: Like): Long = dbTransaction {
        articleDao.updateViaRead(data.articleId) { it.copy(likes = it.likes + 1) }
        Likes.insert { state ->
            state[userId] = data.userId
            state[articleId] = data.articleId
            state[timestamp] = data.timestamp
        }[Likes.id]
    }

    override suspend fun delete(id: Long) {
        dbTransaction {
            read(id)?.let { like ->
                articleDao.updateViaRead(like.articleId) { it.copy(likes = it.likes - 1) }
            }
            Likes.deleteWhere { Likes.id eq id }
        }
    }

    override suspend fun read(id: Long): Like? {
        return dbTransaction {
            Likes.selectAll().where { Likes.id eq id }
                .mapToLike().singleOrNull()
        }
    }

    override suspend fun getAllLikesOfUser(userId: Long): List<Like> = dbTransaction {
        Likes.selectAll().where {
            Likes.userId eq userId
        }.map {
            Like(
                id = it[Likes.id],
                userId = it[Likes.userId],
                articleId = it[Likes.articleId] ?: DELETED_ARTICLE_ID,
                timestamp = it[Likes.timestamp]
            )
        }
    }


    override suspend fun getAllLikesOfArticle(articleId: Long): List<Like> = dbTransaction {
        Likes.selectAll().where {
            Likes.articleId eq articleId
        }.map {
            Like(
                id = it[Likes.id],
                userId = it[Likes.userId],
                articleId = it[Likes.articleId] ?: DELETED_ARTICLE_ID,
                timestamp = it[Likes.timestamp]
            )
        }
    }

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Like> =
        Likes.getPageQuery(pageStart, perPageCount).mapToLike()

    private fun Iterable<ResultRow>.mapToLike(): List<Like> {
        return map {
            Like(
                id = it[Likes.id],
                userId = it[Likes.userId],
                articleId = it[Likes.articleId] ?: DELETED_ARTICLE_ID,
                timestamp = it[Likes.timestamp]
            )
        }
    }
}