package com.example.models

import com.example.util.Default
import com.example.util.empty

@kotlinx.serialization.Serializable
data class Comment(
    val id: Long = Long.Default,

    /**
     * The user ID who commented on this article.
     */
    val userId: Long = Long.Default,

    /**
     * Comment content
     */
    val content: String = empty,

    /**
     * ID of article
     */
    val articleId: Long = Long.Default,

    /**
     * Comment time
     */
    val timestamp: Long = Long.Default,

    @kotlinx.serialization.Transient
    val visibleToOwner: Boolean = true
) : java.io.Serializable
