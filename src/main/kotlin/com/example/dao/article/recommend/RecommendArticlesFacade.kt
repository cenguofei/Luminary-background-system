package com.example.dao.article.recommend

import com.example.models.Article

const val RECOMMEND_TAG = "recommend_articles"

interface RecommendArticlesFacade {
    /**
     * 根据推荐算法返回推荐文章
     */
    suspend fun recommendArticles(
        loginUserId: Long
    ): List<Article>

    companion object : RecommendArticlesFacade by RecommendArticlesFacadeImpl()
}

