package com.example.dao.article

import com.example.dao.audit.AuditDao
import com.example.models.*
import com.example.models.tables.Articles
import com.example.plugins.database.database
import com.example.util.Default
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

class ArticleDaoImpl : ArticleDao {

    init {
        transaction(database) { SchemaUtils.create(Articles) }
    }

    override suspend fun pageCount(): Long = count()

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Article> = dbTransaction {
        val offset = pageOffset(pageStart, perPageCount)
        Articles.selectAll()
            .where(articlePredicate())
            .orderBy(Articles.timestamp, SortOrder.DESC)
            .limit(n = perPageCount, offset = offset)
            .mapToArticle()
    }

    override suspend fun updateViaRead(id: Long, update: (old: Article) -> Article) {
        dbTransaction {
            read(id)?.let { update(id, update(it)) }
        }
    }

    override suspend fun create(data: Article): Long = dbTransaction {
        Articles.insert { state ->
            (state as UpdateBuilder<Int>).setKeyValue(data)
        }[Articles.id]
    }

    override suspend fun delete(id: Long) {
        dbTransaction {
            Articles.deleteWhere { Articles.id eq id }
        }
    }

    override suspend fun update(id: Long, data: Article) {
        dbTransaction {
            Articles.update(where = { Articles.id eq id }) { state ->
                state.setKeyValue(data)
            }
        }
    }

    override suspend fun read(id: Long): Article? {
        return dbTransaction {
            Articles.selectAll().where { Articles.id eq id }
                .mapToArticle().singleOrNull()
        }
    }

    override suspend fun insertBatch(articles: List<Article>): List<Long> {
        return dbTransaction {
            Articles.batchInsert(articles) { article ->
                (this as UpdateBuilder<Int>).setKeyValue(article)
            }.map {
                it[Articles.id]
            }
        }
    }

    override suspend fun getArticlesOfUser(userId: Long): List<Article> {
        return dbTransaction {
            Articles.selectAll().where {
                Articles.userId eq userId
            }.mapToArticle()
        }
    }

    override suspend fun userArticlesOnlyId(userId: Long): List<Long> {
        return dbTransaction {
            Articles.selectAll().where {
                Articles.userId eq userId
            }.map {
                it[Articles.id]
            }
        }
    }

    override suspend fun getArticlesByIds(ids: List<Long>): List<Article> {
        return dbTransaction {
            Articles.selectAll().where {
                Articles.id inList ids
            }.mapToArticle()
        }
    }

    override suspend fun allData(): List<Article> {
        return dbTransaction {
            Articles.selectAll().where(articlePredicate()).mapToArticle()
        }
    }

    override suspend fun getArticles(n: Int, eliminate: List<Long>): List<Article> {
        return dbTransaction {
            Articles.selectAll().where(articlePredicate())
                .limit(n).mapToArticle()
        }
    }

    override suspend fun matchAllByTags(tags: List<String>): List<Article> {
        return dbTransaction {
            Articles.selectAll().where(articlePredicate()).mapToArticle()
                .filter { article ->
                    tags.any { it in article.tags }
                }
        }
    }

    override suspend fun existing(id: Long): Boolean {
        return dbTransaction {
            Articles.selectAll().where {
                Articles.id eq id
            }.limit(1).mapToArticle().isNotEmpty()
        }
    }

    override suspend fun audit(
        oldArticle: Article,
        newArticle: Article,
        auditor: User
    ) {
        dbTransaction {
            update(newArticle.id, newArticle)
            AuditDao.create(
                Audit(
                    auditorId = auditor.id,
                    articleId = newArticle.id,
                    prevState = oldArticle.publishState,
                    toState = newArticle.publishState
                )
            )
        }
    }

    private fun UpdateBuilder<Int>.setKeyValue(article: Article) {
        this[Articles.userId] = article.userId
        this[Articles.username] = article.username
        this[Articles.author] = article.author
        this[Articles.title] = article.title
        this[Articles.link] = article.link
        this[Articles.body] = article.body
        this[Articles.visibleMode] = article.visibleMode.name
        this[Articles.publishState] = article.publishState.name
        this[Articles.tags] = article.tags
        this[Articles.likes] = article.likes
        this[Articles.collections] = article.collections
        this[Articles.comments] = article.comments
        this[Articles.viewsNum] = article.viewsNum
        val ts = if (article.timestamp == Long.Default) System.currentTimeMillis() else article.timestamp
        this[Articles.timestamp] = ts
        this[Articles.cover] = article.cover
    }
}

fun Iterable<ResultRow>.mapToArticle(): List<Article> {
    return map {
        Article(
            id = it[Articles.id],
            userId = it[Articles.userId],
            username = it[Articles.username],
            author = it[Articles.author],
            title = it[Articles.title],
            link = it[Articles.link],
            body = it[Articles.body],
            tags = it[Articles.tags],
            visibleMode = VisibleMode.valueOf(it[Articles.visibleMode]),
            publishState = PublishState.valueOf(it[Articles.publishState]),
            likes = it[Articles.likes],
            collections = it[Articles.collections],
            comments = it[Articles.comments],
            viewsNum = it[Articles.viewsNum],
            timestamp = it[Articles.timestamp],
            cover = it[Articles.cover]
        )
    }
}