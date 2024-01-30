package com.example.dao.article

import com.example.models.Article
import com.example.models.Articles
import com.example.models.Users
import com.example.plugins.database.database
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.dateLiteral
import org.jetbrains.exposed.sql.javatime.timestampLiteral
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ArticleDaoImpl : ArticleDao {

    init { transaction(database) { SchemaUtils.create(Articles) } }

    override suspend fun articles(): List<Article> {
        return emptyList()
    }

    override suspend fun create(article: Article): Long = dbTransaction {
        Articles.insert { state->
            (state as UpdateBuilder<Int>).setKeyValue(article)
        }[Articles.id]
    }

    override suspend fun delete(id: Long) {
        dbTransaction {
            Articles.deleteWhere { Articles.id eq id }
        }
    }

    override suspend fun update(id: Long, article: Article) {
        dbTransaction {
            Articles.update(where = { Articles.id eq id }) { state ->
                (state as UpdateBuilder<Int>).setKeyValue(article)
            }
        }
    }

    override suspend fun read(id: Long): Article? {
        return dbTransaction {
            Articles.selectAll().limit(1).map {
                Article(
                    id = it[Articles.id],
                    userId = it[Articles.userId],
                    username = it[Articles.username],
                    author = it[Articles.author],
                    title = it[Articles.title],
                    link = it[Articles.link],
                    body = it[Articles.body],
                    niceDate = it[Articles.niceDate].format(DateTimeFormatter.ofPattern("uuuu-MM-dd")),
                    tags = it[Articles.tags],
                    visibleMode = it[Articles.visibleMode],
                    likes = it[Articles.likes],
                    collections = it[Articles.collections],
                    comments = it[Articles.comments],
                    viewsNum = it[Articles.viewsNum]
                )
            }.singleOrNull()
        }
    }

    private fun pageArticles() {

    }

    private fun UpdateBuilder<Int>.setKeyValue(article: Article) {
        this[Articles.userId] = article.userId
        this[Articles.username] = article.username
        this[Articles.author] = article.author
        this[Articles.title] = article.title
        this[Articles.link] = article.link
        this[Articles.body] = article.body
        this[Articles.niceDate] = dateLiteral(LocalDate.parse(article.niceDate))
        this[Articles.visibleMode] = article.visibleMode
        this[Articles.tags] = article.tags
        this[Articles.likes] = article.likes
        this[Articles.collections] = article.collections
        this[Articles.comments] = article.comments
        this[Articles.viewsNum] = article.viewsNum
    }
}
