package com.example.dao.article

import com.example.dao.LuminaryDao
import com.example.models.Comment
import com.example.models.tables.Comments

interface CommentDao : LuminaryDao<Comment, Comments> {
    /**
     * 返回用户评论过的文章
     */
    suspend fun getAllCommentsOfUserCommentToArticle(userId: Long): List<Comment>

    /**
     * 返回文章获得的评论
     */
    suspend fun getAllCommentsOfArticle(articleId: Long): List<Comment>

    suspend fun getCommentsByIdsOfArticle(articleIds: List<Long>): List<Comment>

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Comment>

    override suspend fun create(data: Comment): Long

    override suspend fun delete(id: Long)

    override suspend fun read(id: Long): Comment?

    override suspend fun count(): Long = Comments.count()

    companion object : CommentDao by CommentDaoImpl()
}