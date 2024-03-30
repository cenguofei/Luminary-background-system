package com.example.routings.article.comment

import com.example.models.Comment
import com.example.models.User

@kotlinx.serialization.Serializable
data class CommentWithUser(
    val user: User? = null,
    val comments: List<Comment> = emptyList()
)
