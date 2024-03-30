package com.example.dao.article

import com.example.models.Article
import com.example.models.tables.Articles
import com.example.models.tables.Users
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.selectAll

class VisibilityArticlesOfUserDao(
    private val userId: Long,
    private val predicate: () -> Op<Boolean>,
) : DefaultArticleDao() {

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Article> {
        return dbTransaction {
            val offset = pageOffset(pageStart, perPageCount)
            getAllArticles()
                .limit(n = perPageCount, offset = offset)
                .mapToArticle()
        }
    }

    override suspend fun pageCount(): Long {
        return dbTransaction { getAllArticles().count() }
    }

    private suspend fun getAllArticles(): Query {
        return dbTransaction {
            Articles.innerJoin(
                otherTable = Users,
                onColumn = {
                    this.userId
                },
                otherColumn = {
                    this.id
                },
                additionalConstraint = {
                    Users.id eq userId
                }
            ).selectAll().where { predicate() }
        }
    }
}