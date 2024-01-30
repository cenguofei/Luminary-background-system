package com.example.models.responses

import com.example.models.User
import com.example.util.empty

@kotlinx.serialization.Serializable
data class UserResponse(
    val message: String = empty,
    val success: Boolean = false,
    val shouldLogin: Boolean = false,
    val data: User? = null
)