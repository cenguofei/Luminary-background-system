package com.example.dao.article

import com.example.dao.LuminaryDao
import com.example.models.Article
import com.example.models.tables.Articles
import com.example.util.Default
import com.example.util.dbTransaction
import com.example.util.empty
import org.jetbrains.exposed.sql.selectAll

interface ArticleDao : LuminaryDao<Article, Articles> {
    /**
     * @param perPageCount 每一页有多少条数据
     */
    override suspend fun pages(pageStart: Int, perPageCount: Int) : List<Article>

    override suspend fun create(data: Article): Long

    override suspend fun delete(id: Long)

    override suspend fun update(id: Long, data: Article)

    override suspend fun read(id: Long): Article?

    override suspend fun updateViaRead(id: Long, update:(old: Article) -> Article)

    override suspend fun count(): Long = Articles.count()

    suspend fun insertBatch(articles: List<Article>): List<Long>

    suspend fun getArticlesOfUser(userId: Long): List<Article>

    suspend fun getArticlesByIds(ids: List<Long>): List<Article>

    companion object : ArticleDao by ArticleDaoImpl()
}

open class DefaultArticleDao : ArticleDao {
    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Article> {
        return emptyList()
    }

    override suspend fun create(data: Article): Long {
        return Long.Default
    }

    override suspend fun delete(id: Long) {
    }

    override suspend fun update(id: Long, data: Article) {
    }

    override suspend fun read(id: Long): Article? {
        return null
    }

    override suspend fun updateViaRead(id: Long, update: (old: Article) -> Article) {
    }

    override suspend fun insertBatch(articles: List<Article>): List<Long> {
        return emptyList()
    }

    override suspend fun getArticlesOfUser(userId: Long): List<Article> {
        return emptyList()
    }

    override suspend fun getArticlesByIds(ids: List<Long>): List<Article> {
        return emptyList()
    }
}