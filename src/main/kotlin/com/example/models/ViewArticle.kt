package com.example.models

import com.example.util.Default

@kotlinx.serialization.Serializable
data class ViewArticle(
    val id: Long = Long.Default,
    val userId: Long = Long.Default,
    val articleId: Long = Long.Default,
    val timestamp: Long = Long.Default
)
