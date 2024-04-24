package com.example.dao.article.recommend

import com.example.dao.article.ArticleDao
import com.example.dao.topic.userTopics
import com.example.models.Article
import com.example.util.logi

class UserInterested : OtherRecommendSource {
    override suspend fun getArticles(loginUserId: Long, oldArticleIds: List<Long>): List<Article> {
        val userTopics = userTopics(loginUserId)
        return ArticleDao.matchAllByTags(userTopics.map { it.topic })
            .filter { it.id !in oldArticleIds }
            .also { articles ->
                "userInterested Articles:${articles.map { it.id }}".logi(RECOMMEND_TAG)
            }
    }
}