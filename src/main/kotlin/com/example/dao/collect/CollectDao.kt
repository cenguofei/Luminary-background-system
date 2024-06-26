package com.example.dao.collect

import com.example.dao.LunimaryDao
import com.example.models.Collect
import com.example.models.tables.Collects

interface CollectDao : LunimaryDao<Collect, Collects> {
    /**
     * 获取用户收藏的文章
     */
    suspend fun getAllCollectsOfUser(userId: Long) : List<Collect>

    /**
     * 获取收藏此文章的用户
     */
    suspend fun getAllCollectsOfArticle(articleId: Long) : List<Collect>

    override suspend fun create(data: Collect): Long

    override suspend fun delete(id: Long)

    suspend fun delete(collect: Collect)

    override suspend fun read(id: Long): Collect?

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Collect>

    override suspend fun pageCount(): Long

    override suspend fun count(): Long = Collects.count()

    suspend fun exists(collect: Collect): Boolean

    suspend fun friendCollects(friends: List<Long>): List<Collect>

    companion object : CollectDao by CollectDaoImpl()
}