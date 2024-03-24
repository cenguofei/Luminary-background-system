package com.example.models.ext

import com.example.models.User
import kotlinx.serialization.Serializable

@Serializable
data class FollowInfo(
    val myFollow: User,
    val alsoFollowMe: Boolean
)
