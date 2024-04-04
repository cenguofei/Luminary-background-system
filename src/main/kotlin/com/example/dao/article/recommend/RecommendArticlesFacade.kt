package com.example.dao.article.recommend

import com.example.models.Article

const val RECOMMEND_TAG = "recommend_articles"

interface RecommendArticlesFacade {
    /**
     * �����Ƽ��㷨�����Ƽ�����
     */
    suspend fun recommendArticles(
        loginUserId: Long
    ): List<Article>

    companion object : RecommendArticlesFacade by RecommendArticlesFacadeImpl()
}

