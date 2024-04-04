package com.example.dao.article.recommend

import com.example.dao.article.ArticleDao
import com.example.dao.view.duration.ViewDurationDao
import com.example.models.Article

/**
 * 用户浏览过的时长最长的10种类型的文章，根据[Article.tags]区分文章类型
 * 例如tags=["Kotlin", "Android"]，那么这篇文章的类型就为Kotlin活Android
 */
class DurationTop10 : OtherRecommendSource {
    override suspend fun getArticles(loginUserId: Long, oldArticleIds: List<Long>): List<Article> {
        val userDurationsTop10 = ViewDurationDao.userDurationsTop10(loginUserId)
        val types = userDurationsTop10.map { it.type }.distinctBy { it }
        return ArticleDao.matchAllByTags(types).filter {
            it.id !in oldArticleIds
        }
    }
}