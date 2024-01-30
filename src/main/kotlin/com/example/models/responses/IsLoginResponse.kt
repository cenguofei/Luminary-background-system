package com.example.models.responses

@kotlinx.serialization.Serializable
data class IsLoginResponse(
    val message: String? = null,
    val isLogin: Boolean = false
)