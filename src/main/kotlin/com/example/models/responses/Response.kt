package com.example.models.responses

@kotlinx.serialization.Serializable
data class Response(
    val message: String? = null,
    val success: Boolean = false,
)