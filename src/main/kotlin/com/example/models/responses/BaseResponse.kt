package com.example.models.responses

import com.example.util.empty

@kotlinx.serialization.Serializable
open class BaseResponse<T> {
    open var msg: String = empty

    open var data: T? = null
}
