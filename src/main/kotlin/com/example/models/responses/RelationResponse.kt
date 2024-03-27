package com.example.models.responses

import com.example.util.empty

@kotlinx.serialization.Serializable
class RelationResponse<T> : BaseResponse<RelationData<T>>()

@kotlinx.serialization.Serializable
data class RelationData<T>(
    val num: Int = 0,
    val relations: List<T> = emptyList()
)