package com.example.dao.article.recommend

import com.example.models.Article

interface OtherRecommendSource {

    suspend fun getArticles(
        loginUserId: Long,
        oldArticleIds: List<Long>
    ): List<Article>
}