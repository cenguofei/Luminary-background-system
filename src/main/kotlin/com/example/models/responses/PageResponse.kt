package com.example.models.responses

import kotlinx.serialization.Serializable

/**
 * request:
 * http://localhost:8080/article/all?curPage=0&pageCount=12
 */
@Serializable
open class PageResponse<T> : java.io.Serializable, BaseResponse<Page<T>>()

@Serializable
data class Page<T>(
    /**
     * prevPage = curPage - 1
     * nextPage = curPage + 1
     */
    val curPage: Int = 0,

    /**
     * ��ҳ��
     */
    val pageSize: Long = 0,

    /**
     * ��������
     */
    val total: Long = 0,

    val lists: List<T> = emptyList(),
) : java.io.Serializable