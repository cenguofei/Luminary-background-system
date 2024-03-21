package com.example.dao.article

import com.example.dao.LuminaryDao
import com.example.models.Like
import com.example.models.tables.Likes

interface LikeDao : LuminaryDao<Like, Likes> {
    suspend fun getAllLikesOfUser(userId: Long) : List<Like>

    suspend fun getAllLikesOfArticle(articleId: Long) : List<Like>

    override suspend fun create(data: Like): Long

    override suspend fun delete(id: Long)

    suspend fun delete(like: Like)

    override suspend fun read(id: Long): Like?

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Like>

    override suspend fun count(): Long = Likes.count()

    /**
     * 用户的所有文章共获得了多少点赞
     * @param userId 用户id
     */
    suspend fun likesNumOfUserArticles(userId: Long, articlesId: List<Long>): Long

    suspend fun exists(other: Like): Boolean

    suspend fun getLikesInArticleIds(articleIds: List<Long>): List<Like>

    companion object : LikeDao by LikeDaoImpl()
}