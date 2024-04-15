package com.example.dao.article.recommend

import com.example.dao.article.ArticleDao
import com.example.models.Article

class RecommendFacadeImpl : RecommendFacade {
    override suspend fun recommendArticles(
        loginUserId: Long
    ): List<Article> {
        val byFriendEngine: RecommendationByFriendEngine = RecommendationByFriendEngineImpl(
            articles = ArticleDao.allData()
        )
        return byFriendEngine.recommendArticles(loginUserId)
    }
}