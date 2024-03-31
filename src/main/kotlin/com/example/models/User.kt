package com.example.models

import com.example.util.Default
import com.example.util.empty
import io.ktor.server.auth.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long = Long.Default,
    val username: String,
    val age: Int = Int.Default,
    val sex: Sex = Sex.Sealed,
    @SerialName("head_url")
    val headUrl: String = "res/uploads/default_bg.jpg",
    val background: String = "res/uploads/default_head_img.png",
    val password: String = empty,
    val role: Role = Role.User,
    val status: UserStatus = UserStatus.Normal,

    val birth: Long = Long.Default,
    val signature: String = empty,
    val location: String = empty,
    @SerialName("blog_address")
    val blogAddress: String = "https://github.com/cenguofei"
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
