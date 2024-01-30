package com.example.dao.article

import com.example.models.Article
import com.example.models.Articles
import org.jetbrains.exposed.sql.selectAll

interface ArticleDao {
    suspend fun articles() : List<Article>

    suspend fun create(article: Article) : Long

    suspend fun delete(id: Long)

    suspend fun update(id: Long, article: Article)

    suspend fun read(id: Long): Article?

    fun count(): Long = Articles.selectAll().count()
}