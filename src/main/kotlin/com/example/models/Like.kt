package com.example.models

import com.example.util.Default

@kotlinx.serialization.Serializable
data class Like(
    val likeUserId: Long = Long.Default
)
