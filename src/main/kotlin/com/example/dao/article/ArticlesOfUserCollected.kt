package com.example.dao.article

import com.example.models.Article
import com.example.models.VisibleMode
import com.example.models.tables.Articles
import com.example.models.tables.Collects
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll

class ArticlesOfUserCollected(
    private val userId: Long
) : DefaultArticleDao() {
    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Article> {
        return dbTransaction {
            all()
                .limit(n = perPageCount, offset = pageOffset(pageStart, perPageCount))
                .mapToArticle()
        }
    }

    override suspend fun pageCount(): Long {
        return dbTransaction { all().count() }
    }

    private suspend fun all() = dbTransaction {
        Articles.innerJoin(
            otherTable = Collects,
            onColumn = {
                this.id
            },
            otherColumn = {
                this.articleId
            },
            additionalConstraint = {
                Collects.collectUserId eq userId
            }
        ).selectAll().where {
            (Articles.visibleMode eq VisibleMode.PUBLIC.name) or (Articles.userId eq userId)
        }
    }
}