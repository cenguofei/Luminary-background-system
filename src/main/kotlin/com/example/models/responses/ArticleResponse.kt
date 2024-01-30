package com.example.models.responses

import com.example.models.Article

/**
 * request:
 * http://localhost:8080/article/all?curPage=0&pageCount=12
 */
@kotlinx.serialization.Serializable
data class ArticleResponse(
    /**
     * prevPage = curPage - 1
     * nextPage = curPage + 1
     */
    val curPage: Int = 0,

    /**
     * ��ҳ��
     */
    val pageSize: Int = 0,

    /**
     * ÿһҳ�ж�������
     */
    val pageCount: Int = PAGE_COUNT,

    /**
     * ���ݿ������µ�����
     */
    val totalArticle: Int = 0,

    val data: List<Article> = emptyList()
)

const val PAGE_COUNT = 12
