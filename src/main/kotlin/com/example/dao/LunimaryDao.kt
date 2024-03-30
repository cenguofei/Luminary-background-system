package com.example.dao

import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.*

interface LunimaryDao<T, P: Table> : LunimaryPage<T> {

    override suspend fun pageCount(): Long

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<T>

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

    suspend fun updateViaRead(id: Long, update:(old: T) -> T) { throw UnsupportedOperationException() }

    suspend fun existing(id: Long) : Boolean = throw UnsupportedOperationException()

    suspend fun P.count(): Long = dbTransaction { selectAll().count() }

    suspend fun count(): Long = throw UnsupportedOperationException()

    suspend fun P.getPageQuery(pageStart: Int, perPageCount: Int) : Query {
        val offset = pageOffset(pageStart, perPageCount)
        return dbTransaction {
            selectAll().limit(n = perPageCount, offset = offset)
        }
    }

    suspend fun P.getPageQuery(
        pageStart: Int,
        perPageCount: Int,
        where: SqlExpressionBuilder.() -> Op<Boolean>
    ) : Query {
        val offset = pageOffset(pageStart, perPageCount)
        return dbTransaction {
            selectAll()
                .where(where)
                .limit(n = perPageCount, offset = offset)
        }
    }
}