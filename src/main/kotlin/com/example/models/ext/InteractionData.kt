package com.example.models.ext

@kotlinx.serialization.Serializable
data class InteractionData(
    val likeNum: Long = 0, //��������
    val friendNum: Long = 0, //��������
    val followingNum: Long = 0, //��ע����
    val followerNum: Long = 0, //��˿����
)
