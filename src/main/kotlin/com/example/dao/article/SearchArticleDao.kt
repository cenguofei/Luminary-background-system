package com.example.dao.article

import com.example.models.Article
import com.example.models.tables.Articles
import com.example.models.tables.Users
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll

class SearchArticleDao(
    private val searchContent: String
) : DefaultArticleDao() {

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Article> {
        return dbTransaction {
            Articles.innerJoin(
                otherTable = Users,
                onColumn = {
                    this.userId
                },
                otherColumn = {
                    this.id
                }
            ).selectAll()
                .where {
                    (Articles.title like "%$searchContent%") or (Articles.body like "%$searchContent%") or (Users.username like "%$searchContent%")
                }.limit(n = perPageCount, offset = pageOffset(pageStart, perPageCount))
                .mapToArticle()
        }
    }

    override suspend fun pageCount(): Long = dbTransaction {
        Articles.selectAll().where {
            (Articles.title like "%$searchContent%") or (Articles.body like "%$searchContent%")
        }.count()
    }
}