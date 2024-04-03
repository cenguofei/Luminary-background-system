package com.example.dao.article.recommend

import com.example.dao.article.ArticleDao.Companion.getPageQuery
import com.example.dao.article.mapToArticle
import com.example.models.Article
import com.example.models.VisibleMode
import com.example.models.tables.Articles
import com.example.util.dbTransaction

class RecommendImpl : Recommend {
    override suspend fun recommendArticles(): List<Article> = dbTransaction {
        Articles.getPageQuery( // TODO
            pageStart = 0,
            perPageCount = 12,
            where = {
                Articles.visibleMode eq VisibleMode.PUBLIC.name
            }
        ).mapToArticle()
    }
}