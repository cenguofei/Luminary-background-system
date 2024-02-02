package com.example.dao.article

import com.example.dao.LuminaryDao
import com.example.models.Collect
import com.example.models.Like

interface CollectDao : LuminaryDao<Collect> {
    suspend fun getAllCollectsOfUser(userId: Long) : List<Collect>

    suspend fun getAllCollectsOfArticle(articleId: Long) : List<Collect>
}