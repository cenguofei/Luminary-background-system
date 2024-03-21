package com.example.dao.article

import com.example.dao.LuminaryDao
import com.example.models.Article
import com.example.models.tables.Articles

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