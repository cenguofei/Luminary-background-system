package com.example.dao.article

import com.example.dao.LuminaryDao
import com.example.models.Article
import com.example.models.tables.Articles
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.selectAll

interface ArticleDao : LuminaryDao<Article> {
    /**
     * @param perPageCount 每一页有多少条数据
     */
    suspend fun pages(pageStart: Int, perPageCount: Int) : List<Article>

    override suspend fun create(data: Article): Long

    override suspend fun delete(id: Long)

    override suspend fun update(id: Long, data: Article)

    override suspend fun read(id: Long): Article?

    override suspend fun updateViaRead(id: Long, update:(old: Article) -> Article)

    suspend fun count(): Long = dbTransaction { Articles.selectAll().count() }
}