package com.example.dao.article.recommend

import com.example.dao.article.ArticleDao
import com.example.models.Article

class RecommendArticlesFacadeImpl : RecommendArticlesFacade {
    override suspend fun recommendArticles(
        loginUserId: Long
    ): List<Article> {
        val byFriendEngine: RecommendationByFriendEngine = RecommendationByFriendEngineImpl(
            articles = ArticleDao.allData()
        )
        return byFriendEngine.recommendArticles(loginUserId)
    }
}