package com.example.dao.article.recommend

import com.example.models.Article

const val RECOMMEND_TAG = "recommend_articles"

interface RecommendFacade {
    /**
     * �����Ƽ��㷨�����Ƽ�����
     */
    suspend fun recommendArticles(
        loginUserId: Long
    ): List<Article>

    companion object : RecommendFacade by RecommendFacadeImpl()
}

