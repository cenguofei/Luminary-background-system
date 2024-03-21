package com.example.models.ext

import com.example.models.User

@kotlinx.serialization.Serializable
data class UserFriend(
    val user: User,
    val beFriendTime: Long
)