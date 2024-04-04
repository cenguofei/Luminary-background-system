package com.example.dao.view

import com.example.dao.LunimaryDao
import com.example.models.ViewArticle
import com.example.models.tables.ViewArticles

interface ViewArticleDao : LunimaryDao<ViewArticle, ViewArticles> {

    override suspend fun create(data: ViewArticle): Long

    suspend fun exist(viewArticle: ViewArticle): Boolean

    /**
     * 用户朋友浏览过的文章
     * @param friends 朋友id
     */
    suspend fun friendsViewArticles(friends: List<Long>): List<ViewArticle>

    companion object : ViewArticleDao by ViewArticleDaoImpl()
}