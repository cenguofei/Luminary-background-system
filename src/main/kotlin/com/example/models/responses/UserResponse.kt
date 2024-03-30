package com.example.models.responses

import com.example.models.User
import kotlinx.serialization.Serializable

@Serializable
class UserResponse: BaseResponse<UserData>()

@Serializable
data class UserData(
    val shouldLogin: Boolean = false,
    val user: User? = null
) : java.io.Serializable