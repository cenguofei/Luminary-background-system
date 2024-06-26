package com.example.dao.comment

import com.example.dao.article.ArticleDao
import com.example.models.Comment
import com.example.models.tables.Comments
import com.example.models.tables.DELETED_ARTICLE_ID
import com.example.plugins.database.database
import com.example.util.Default
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class CommentDaoImpl : CommentDao {
    init {
        transaction(database) { SchemaUtils.create(Comments) }
    }

    private val articleDao = ArticleDao

    override suspend fun create(data: Comment): Long = dbTransaction {
        articleDao.updateViaRead(data.articleId) { it.copy(comments = it.comments + 1) }
        Comments.insert { state ->
            state[userId] = data.userId
            state[content] = data.content
            val ts = if (data.timestamp == Long.Default) System.currentTimeMillis() else data.timestamp
            state[timestamp] = ts
            state[articleId] = data.articleId
            state[visibleToOwner] = data.visibleToOwner
        }[Comments.id]
    }

    override suspend fun delete(id: Long) {
        dbTransaction {
            read(id)?.let { collect ->
                articleDao.updateViaRead(collect.articleId) { it.copy(comments = it.comments - 1) }
            }
            Comments.deleteWhere { Comments.id eq id }
        }
    }

    override suspend fun read(id: Long): Comment? {
        return dbTransaction {
            Comments.selectAll()
                .where { Comments.id eq id }
                .limit(1)
                .mapToComment()
                .singleOrNull()
        }
    }

    override suspend fun getAllCommentsOfUserCommentToArticle(userId: Long): List<Comment> = dbTransaction {
        Comments.selectAll().where {
            Comments.userId eq userId
        }.mapToComment()
    }

    override suspend fun getAllCommentsOfArticle(articleId: Long): List<Comment> = dbTransaction {
        Comments.selectAll().where {
            Comments.articleId eq articleId
        }.mapToComment()
    }

    override suspend fun getCommentsByIdsOfArticle(articleIds: List<Long>): List<Comment> {
        return dbTransaction {
            Comments.selectAll().where {
                (Comments.visibleToOwner eq true) and (Comments.articleId inList articleIds)
            }.mapToComment()
        }
    }

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Comment> =
        Comments.getPageQuery(
            pageStart = pageStart,
            perPageCount = perPageCount,
            where = {
                Comments.visibleToOwner eq true
            }
        ).mapToComment()

    override suspend fun pageCount(): Long {
        return dbTransaction {
            Comments.selectAll().where {
                Comments.visibleToOwner eq true
            }.count()
        }
    }

    override suspend fun friendComments(friends: List<Long>): List<Comment> {
        return dbTransaction {
            Comments.selectAll().where {
                Comments.userId inList friends
            }.mapToComment()
        }
    }

    override suspend fun update(data: Comment) {
        dbTransaction {
            Comments.update(
                where = { Comments.id eq data.id },
                limit = 1
            ) { state ->
                state[visibleToOwner] = data.visibleToOwner
            }
        }
    }
}

fun Iterable<ResultRow>.mapToComment(): List<Comment> {
    return map {
        Comment(
            id = it[Comments.id],
            articleId = it[Comments.articleId] ?: DELETED_ARTICLE_ID,
            userId = it[Comments.userId],
            content = it[Comments.content],
            timestamp = it[Comments.timestamp],
            visibleToOwner = it[Comments.visibleToOwner]
        )
    }
}