package com.example.dao.article

import com.example.dao.LuminaryDao
import com.example.models.Collect
import com.example.models.Like
import com.example.models.tables.Collects

interface CollectDao : LuminaryDao<Collect, Collects> {
    suspend fun getAllCollectsOfUser(userId: Long) : List<Collect>

    suspend fun getAllCollectsOfArticle(articleId: Long) : List<Collect>

    override suspend fun create(data: Collect): Long

    override suspend fun delete(id: Long)

    override suspend fun read(id: Long): Collect?

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Collect>

    override suspend fun count(): Long = Collects.count()
}