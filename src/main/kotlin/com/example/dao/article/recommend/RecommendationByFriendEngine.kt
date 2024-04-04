package com.example.dao.article.recommend

import com.example.models.Article

interface RecommendationByFriendEngine {
    /**
     * �����Ƽ�����
     */
    suspend fun recommendArticles(
        loginUserId: Long
    ): List<Article>
}