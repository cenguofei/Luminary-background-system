package com.example.models.responses

import com.example.util.empty

@kotlinx.serialization.Serializable
open class BaseResponse<T> : java.io.Serializable {
    open var msg: String = empty

    open var data: T? = null

    fun copy(
        msg: String = empty,
        data: T? = null
    ) : BaseResponse<T> {
        this.msg = msg
        this.data = data
        return this
    }
}
