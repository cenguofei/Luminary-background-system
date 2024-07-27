package com.example.dao.like

import com.example.dao.article.DefaultArticleDao
import com.example.dao.article.articlePredicate
import com.example.dao.article.mapToArticle
import com.example.models.Article
import com.example.models.VisibleMode
import com.example.models.tables.Articles
import com.example.models.tables.Likes
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll

class ArticlesOfUserLiked(
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
            otherTable = Likes,
            onColumn = {
                this.id
            },
            otherColumn = {
                this.articleId
            },
            additionalConstraint = {
                Likes.userId eq userId
            }
        ).selectAll().where {
            articlePredicate() or (Articles.userId eq userId)
        }
    }
}