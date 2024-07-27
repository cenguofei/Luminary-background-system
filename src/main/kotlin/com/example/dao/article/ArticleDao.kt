package com.example.dao.article

import com.example.dao.LunimaryDao
import com.example.models.Article
import com.example.models.PublishState
import com.example.models.User
import com.example.models.VisibleMode
import com.example.models.tables.Articles
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll

interface ArticleDao : LunimaryDao<Article, Articles> {
    /**
     * @param perPageCount 每一页有多少条数据
     */
    override suspend fun pages(pageStart: Int, perPageCount: Int) : List<Article>

    override suspend fun pageCount(): Long

    override suspend fun create(data: Article): Long

    override suspend fun delete(id: Long)

    override suspend fun update(id: Long, data: Article)

    override suspend fun read(id: Long): Article?

    override suspend fun updateViaRead(id: Long, update:(old: Article) -> Article)

    override suspend fun count(): Long = dbTransaction {
        Articles.selectAll().where(articlePredicate()).count()
    }

    suspend fun insertBatch(articles: List<Article>): List<Long>

    suspend fun getArticlesOfUser(userId: Long): List<Article>

    suspend fun userArticlesOnlyId(userId: Long): List<Long>

    suspend fun getArticlesByIds(ids: List<Long>): List<Article>

    /**
     * 获取数量为[n]的文章
     * @return 返回文章的ID不在[eliminate]内
     */
    suspend fun getArticles(n: Int, eliminate: List<Long>): List<Article>

    /**
     * 查询包含[tags]的文章
     */
    suspend fun matchAllByTags(tags: List<String>): List<Article>

    suspend fun audit(
        oldArticle: Article,
        newArticle: Article,
        auditor: User
    )

    companion object : ArticleDao by ArticleDaoImpl()
}

fun articlePredicate(): SqlExpressionBuilder.() -> Op<Boolean> = {
    Articles.visibleMode.eq(VisibleMode.PUBLIC.name)
        .and(Articles.publishState.eq(PublishState.Approved.name))
}

fun SqlExpressionBuilder.articlePredicate(): Op<Boolean> {
    return com.example.dao.article.articlePredicate().invoke(this)
}