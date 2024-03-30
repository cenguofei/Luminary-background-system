package com.example.dao

import com.example.util.Default

interface LunimaryPage<T> {
    suspend fun pages(pageStart: Int, perPageCount: Int) : List<T>

    suspend fun pageCount(): Long

    fun pageOffset(pageStart: Int, perPageCount: Int) : Long = (pageStart * perPageCount).toLong()

    fun <T> List<T>.page(pageStart: Int, perPageCount: Int): List<T> {
        val offset = pageOffset(pageStart, perPageCount).toInt()
        val fromIndex = offset + Int.Default
        var toIndex = offset + perPageCount
        if (fromIndex >= size) {
            return emptyList()
        }
        if (toIndex > size) {
            toIndex = size
        }
        return subList(fromIndex = fromIndex, toIndex = toIndex)
    }
}