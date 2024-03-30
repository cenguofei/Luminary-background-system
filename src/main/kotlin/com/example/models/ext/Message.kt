package com.example.models.ext

import com.example.models.Article
import com.example.models.Comment
import com.example.models.User

@kotlinx.serialization.Serializable
data class CommentItem(
    val user: User,
    val article: Article,
    val comment: Comment
)

typealias CommentItems = List<CommentItem>