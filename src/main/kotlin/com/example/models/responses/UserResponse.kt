package com.example.models.responses

import com.example.models.User
import com.example.util.empty
import kotlinx.serialization.Serializable

@Serializable
class UserResponse: BaseResponse<UserData>() {
    fun copy(
        msg: String = empty,
        data: UserData? = null
    ) : UserResponse {
        this.msg = msg
        this.data = data
        return this
    }
}

@Serializable
data class UserData(
    val shouldLogin: Boolean = false,
    val user: User? = null
)