package com.example.dao.comment

import com.example.dao.LunimaryDao
import com.example.models.Comment
import com.example.models.tables.Comments

interface CommentDao : LunimaryDao<Comment, Comments> {
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

    override suspend fun pageCount(): Long

    override suspend fun create(data: Comment): Long

    override suspend fun delete(id: Long)

    override suspend fun read(id: Long): Comment?

    override suspend fun count(): Long = Comments.count()

    suspend fun friendComments(friends: List<Long>): List<Comment>

    companion object : CommentDao by CommentDaoImpl()
}