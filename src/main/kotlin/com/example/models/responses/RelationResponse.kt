package com.example.models.responses

import com.example.util.empty

@kotlinx.serialization.Serializable
class RelationResponse<T> : BaseResponse<RelationData<T>>() {
    /**
     * ���ѡ���ע�����˿������
     */
    fun copy(
        msg: String = empty,
        data: RelationData<T>? = null
    ): RelationResponse<T> {
        this.msg = msg
        this.data = data
        return this
    }
}

@kotlinx.serialization.Serializable
data class RelationData<T>(
    val num: Int = 0,
    val relations: List<T> = emptyList()
)