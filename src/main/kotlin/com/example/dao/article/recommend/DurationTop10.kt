package com.example.dao.article.recommend

import com.example.dao.article.ArticleDao
import com.example.dao.view.duration.ViewDurationDao
import com.example.models.Article

/**
 * �û��������ʱ�����10�����͵����£�����[Article.tags]������������
 * ����tags=["Kotlin", "Android"]����ô��ƪ���µ����;�ΪKotlin��Android
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