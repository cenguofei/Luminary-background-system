package com.example.models

import com.example.util.Default

@kotlinx.serialization.Serializable
data class Audit(
    val id: Long = Long.Default,

    val auditorId: Long = Long.Default, // �����ID

    val articleId: Long = Long.Default,

    val prevState: PublishState = PublishState.Auditing,

    val toState: PublishState = PublishState.Auditing,

    val timestamp: Long = System.currentTimeMillis(),
)
