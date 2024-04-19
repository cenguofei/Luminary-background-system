package com.example.util

import org.jetbrains.exposed.crypt.Algorithms

private val Encryptor = Algorithms.AES_256_PBE_GCM(
    password = "fa45bc5479fe2ab548a5d8f94ea2ca5790ca",
    salt = "d1ab89fd4a"
)

fun encrypt(origin: String): String {
    "encrypt $origin".logd("crypt")
    return Encryptor.encrypt(origin)
}

fun decrypt(encrypted: String): String {
    "decrypt $encrypted".logd("crypt")
    return Encryptor.decrypt(encrypted)
}

fun main() {
    val pwd = "1234567"
    val encrypt = Encryptor.encrypt(pwd)
    val decrypt = Encryptor.decrypt(encrypt)
    println("encrypt = $encrypt")
    println("decrypt = $decrypt")
    println(pwd == decrypt)
    println(encrypt == Encryptor.encrypt(pwd))

    val list = listOf(
        "qs1WSQbKH/qAcw29dq8zU0AvcyUDwdhSxNkIrWsZChYP8rwy++Qu",
        "F3Z0bzoJj4S/NtUFvtUfwB5xV0W14lZwUi0sZMj/3qiET+kRDX4n",
        "SRF5Nkpp1SxcUi3D6j7R0nCl9tbX+siQMXA31Xll85U1QtIm5QrW"
    )
    println("de")
    list.forEach {
        println(pwd == Encryptor.decrypt(it))
    }
}