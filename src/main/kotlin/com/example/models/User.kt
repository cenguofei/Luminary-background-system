package com.example.models

import io.ktor.server.auth.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.ehcache.spi.serialization.Serializer
import java.nio.ByteBuffer

@Serializable
data class User(
    val id: Long = 0,
    val username: String,
    val age: Int = 0,
    val sex: Sex = Sex.Sealed,
    @SerialName("head_url")
    val headUrl: String = "123.png",
    val password: String = "",
    val role: Role = Role.User,
    val status: UserStatus = UserStatus.Normal
) : Principal, java.io.Serializable

enum class Sex {
    Male,
    Female,
    Sealed
}

enum class Role {
    User,  //��ͨ�û�
    Manager //����Ա
}

enum class UserStatus {
    Normal,
    Deleted
}
