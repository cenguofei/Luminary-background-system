package com.example.dao.article

import com.example.dao.LuminaryDao
import com.example.models.Comment
import com.example.models.tables.Comments

interface CommentDao : LuminaryDao<Comment, Comments> {
    /**
     * �����û����۹�������
     */
    suspend fun getAllCommentsOfUserCommentToArticle(userId: Long): List<Comment>

    /**
     * �������»�õ�����
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