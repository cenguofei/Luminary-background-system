package com.example.dao

import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll

interface LuminaryDao<T, P: Table> {
    /**
     * 插入一条数据
     */
    suspend fun create(data: T) : Long = throw UnsupportedOperationException()

    /**
     * 根据id删除一条数据
     */
    suspend fun delete(id: Long) { throw UnsupportedOperationException() }

    /**
     * 根据id更新数据
     */
    suspend fun update(id: Long, data: T) { throw UnsupportedOperationException() }

    /**
     * 获取一条数据
     */
    suspend fun read(id: Long): T? = throw UnsupportedOperationException()

    suspend fun pages(pageStart: Int, perPageCount: Int) : List<T> = throw UnsupportedOperationException()

    suspend fun updateViaRead(id: Long, update:(old: T) -> T) { throw UnsupportedOperationException() }

    suspend fun existing(id: Long) : Boolean = throw UnsupportedOperationException()

    suspend fun P.count(): Long = dbTransaction { selectAll().count() }

    suspend fun count(): Long = throw UnsupportedOperationException()

    fun pageOffset(pageStart: Int, perPageCount: Int) : Long = (pageStart * perPageCount).toLong()

    suspend fun P.getPageQuery(pageStart: Int, perPageCount: Int) : Query {
        val offset = pageOffset(pageStart, perPageCount)
        return dbTransaction {
            selectAll().limit(n = perPageCount, offset = offset)
        }
    }
}