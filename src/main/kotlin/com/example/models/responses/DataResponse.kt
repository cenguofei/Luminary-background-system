package com.example.models.responses

import com.example.util.empty

@kotlinx.serialization.Serializable
data class DataResponse<T>(
    val msg: String = empty,
    val success: Boolean = false,
    val data: T? = null
)
