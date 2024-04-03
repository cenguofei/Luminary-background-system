package com.example.models.ext

import com.example.models.Comment
import com.example.models.User

@kotlinx.serialization.Serializable
data class CommentWithUser(
    val user: User? = null,
    val comments: List<Comment> = emptyList()
)
