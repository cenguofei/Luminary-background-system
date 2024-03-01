package com.example.models.responses

import com.example.util.empty

@kotlinx.serialization.Serializable
class DataResponse<T>: BaseResponse<T>() {
    fun copy(
        msg: String = empty,
        data: T? = null
    ) : DataResponse<T> {
        this.msg = msg
        this.data = data
        return this
    }
}
