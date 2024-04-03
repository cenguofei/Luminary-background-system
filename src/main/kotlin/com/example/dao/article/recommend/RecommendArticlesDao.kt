package com.example.dao.article.recommend

import com.example.dao.article.DefaultArticleDao
import com.example.models.Article
import com.example.util.dbTransaction

class RecommendArticlesDao(
    private val userId: Long
) : DefaultArticleDao() {

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Article> = dbTransaction {
        getAllRecommend()
        recommendList.page(pageStart, perPageCount)
    }

    override suspend fun pageCount(): Long {
        getAllRecommend()
        return recommendList.size.toLong()
    }

    private var _recommendList: List<Article>? = null
    private val recommendList: List<Article> get() = _recommendList!!
    private suspend fun getAllRecommend() {
        if (_recommendList != null) {
            return
        }
        _recommendList = RecommendImpl().recommendArticles()
    }
}