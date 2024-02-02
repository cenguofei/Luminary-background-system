package com.example.dao

import com.example.models.Article
import com.example.util.Default

interface LuminaryDao<T> {
    /**
     * 插入一条数据
     */
    suspend fun create(data: T) : Long = Long.Default

    /**
     * 根据id删除一条数据
     */
    suspend fun delete(id: Long) { }


    /**
     * 根据id更新数据
     */
    suspend fun update(id: Long, data: T) { }

    /**
     * 获取一条数据
     */
    suspend fun read(id: Long): T? = null

    suspend fun updateViaRead(id: Long, update:(old: T) -> T) { }
}