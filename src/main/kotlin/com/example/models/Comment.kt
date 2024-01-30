package com.example.models

import com.example.util.empty

@kotlinx.serialization.Serializable
data class Comment(
    val comment: String = empty
)
