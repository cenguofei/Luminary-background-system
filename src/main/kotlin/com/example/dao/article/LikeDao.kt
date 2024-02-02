package com.example.dao.article

import com.example.dao.LuminaryDao
import com.example.models.Like

interface LikeDao : LuminaryDao<Like> {
    suspend fun getAllLikesOfUser(userId: Long) : List<Like>

    suspend fun getAllLikesOfArticle(articleId: Long) : List<Like>
}