package com.example.routings.wanandroid

import com.example.models.Article

@kotlinx.serialization.Serializable
data class WanandroidArticles(
    val articles: List<Article> = emptyList()
)
