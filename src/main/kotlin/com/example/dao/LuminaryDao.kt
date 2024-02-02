package com.example.dao

import com.example.models.Article
import com.example.util.Default

interface LuminaryDao<T> {
    /**
     * ����һ������
     */
    suspend fun create(data: T) : Long = Long.Default

    /**
     * ����idɾ��һ������
     */
    suspend fun delete(id: Long) { }


    /**
     * ����id��������
     */
    suspend fun update(id: Long, data: T) { }

    /**
     * ��ȡһ������
     */
    suspend fun read(id: Long): T? = null

    suspend fun updateViaRead(id: Long, update:(old: T) -> T) { }
}