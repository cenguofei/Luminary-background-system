package com.example.dao.article.recommend

import com.example.models.Article

interface Recommend {
    /**
     * �����Ƽ��㷨�����Ƽ�����
     */
    suspend fun recommendArticles(): List<Article>
}

