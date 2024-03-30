package com.example.models.ext

import com.example.models.User
import kotlinx.serialization.Serializable

@Serializable
data class FollowersInfo(
    val follower: User,
    val alsoFollow: Boolean
) : java.io.Serializable
