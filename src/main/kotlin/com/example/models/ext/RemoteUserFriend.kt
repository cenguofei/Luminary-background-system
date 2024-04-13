package com.example.models.ext

import com.example.models.User

@kotlinx.serialization.Serializable
data class RemoteUserFriend(
    val user: User,
    val beFriendTime: Long,

    val visibleToOwner: Boolean
) : java.io.Serializable

@kotlinx.serialization.Serializable
data class ClientUserFriend(
    val user: User,
    val beFriendTime: Long,
) : java.io.Serializable