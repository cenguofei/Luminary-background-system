package com.example.dao.article.recommend

import com.example.models.Article

interface Recommend {
    /**
     * 根据推荐算法返回推荐文章
     */
    suspend fun recommendArticles(): List<Article>
}

