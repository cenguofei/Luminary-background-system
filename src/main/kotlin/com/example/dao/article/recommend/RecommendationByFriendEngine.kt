package com.example.dao.article.recommend

import com.example.models.Article

interface RecommendationByFriendEngine {
    /**
     * ·µ»ØÍÆ¼öÎÄÕÂ
     */
    suspend fun recommendArticles(
        loginUserId: Long
    ): List<Article>
}