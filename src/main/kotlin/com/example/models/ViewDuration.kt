package com.example.models

import com.example.util.Default
import com.example.util.empty

/**
 * 用户浏览文章时长类，用于推荐文章
 */
@kotlinx.serialization.Serializable
data class ViewDuration(
    val id: Long = Long.Default,

    val userId: Long = Long.Default,

    val type: String = empty,

    val duration: Long = Long.Default,

    val timestamp: Long = Long.Default
)