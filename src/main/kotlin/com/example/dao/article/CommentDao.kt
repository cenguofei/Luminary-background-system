package com.example.dao.article

import com.example.dao.LuminaryDao
import com.example.models.Collect
import com.example.models.Comment

interface CommentDao : LuminaryDao<Comment> {
    suspend fun getAllCommentsOfUser(userId: Long) : List<Comment>

    suspend fun getAllCommentsOfArticle(articleId: Long) : List<Comment>
}