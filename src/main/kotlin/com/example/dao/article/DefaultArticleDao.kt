package com.example.dao.article

import com.example.models.Article
import com.example.models.User
import com.example.util.Default

open class DefaultArticleDao : ArticleDao {
    override suspend fun matchAllByTags(tags: List<String>): List<Article> {
        return emptyList()
    }

    override suspend fun userArticlesOnlyId(userId: Long): List<Long> {
        return emptyList()
    }

    override suspend fun getArticles(n: Int, eliminate: List<Long>): List<Article> {
        return emptyList()
    }

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Article> {
        return emptyList()
    }

    override suspend fun pageCount(): Long {
        return Long.Default
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

    override suspend fun audit(oldArticle: Article, newArticle: Article, auditor: User) {
    }
}