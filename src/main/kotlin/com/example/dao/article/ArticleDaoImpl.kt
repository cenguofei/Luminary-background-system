package com.example.dao.article

import com.example.models.Article
import com.example.models.VisibleMode
import com.example.models.tables.Articles
import com.example.plugins.database.database
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.dateTimeLiteral
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ArticleDaoImpl : ArticleDao {

    init {
        transaction(database) { SchemaUtils.create(Articles) }
    }

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Article> = dbTransaction {
        Articles.getPageQuery(pageStart, perPageCount).mapToArticle()
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

    private fun UpdateBuilder<Int>.setKeyValue(article: Article) {
        this[Articles.userId] = article.userId
        this[Articles.username] = article.username
        this[Articles.author] = article.author
        this[Articles.title] = article.title
        this[Articles.link] = article.link
        this[Articles.body] = article.body
        this[Articles.niceDate] = dateTimeLiteral(LocalDateTime.parse(article.niceDate, formatterToSeconds))
        this[Articles.visibleMode] = article.visibleMode.name
        this[Articles.tags] = article.tags
        this[Articles.likes] = article.likes
        this[Articles.collections] = article.collections
        this[Articles.comments] = article.comments
        this[Articles.viewsNum] = article.viewsNum
        this[Articles.pictures] = article.pictures
    }

    private fun Iterable<ResultRow>.mapToArticle(): List<Article> {
        return map {
            Article(
                id = it[Articles.id],
                userId = it[Articles.userId],
                username = it[Articles.username],
                author = it[Articles.author],
                title = it[Articles.title],
                link = it[Articles.link],
                body = it[Articles.body],
                niceDate = it[Articles.niceDate].format(formatterToSeconds),
                tags = it[Articles.tags],
                visibleMode = VisibleMode.valueOf(it[Articles.visibleMode]),
                likes = it[Articles.likes],
                collections = it[Articles.collections],
                comments = it[Articles.comments],
                viewsNum = it[Articles.viewsNum],
                pictures = it[Articles.pictures]
            )
        }
    }
}

val formatterToSeconds: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")