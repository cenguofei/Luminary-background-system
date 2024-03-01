package com.example.dao.article

import com.example.dao.LuminaryDao
import com.example.models.Like
import com.example.models.tables.Likes

interface LikeDao : LuminaryDao<Like, Likes> {
    suspend fun getAllLikesOfUser(userId: Long) : List<Like>

    suspend fun getAllLikesOfArticle(articleId: Long) : List<Like>

    override suspend fun create(data: Like): Long

    override suspend fun delete(id: Long)

    override suspend fun read(id: Long): Like?

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Like>

    override suspend fun count(): Long = Likes.count()
}