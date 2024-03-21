package com.example.models

import com.example.util.empty
import io.ktor.server.auth.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long = 0,
    val username: String,
    val age: Int = 0,
    val sex: Sex = Sex.Sealed,
    @SerialName("head_url")
    val headUrl: String = "123.png",
    val background: String = "123.png",
    val password: String = "",
    val role: Role = Role.User,
    val status: UserStatus = UserStatus.Normal
) : Principal, java.io.Serializable {

    fun ofNoPassword(): User = this.copy(password = empty)
}

enum class Sex {
    Male,
    Female,
    Sealed
}

enum class Role {
    User,  //普通用户
    Manager //管理员
}

enum class UserStatus {
    Normal,
    Deleted
}
