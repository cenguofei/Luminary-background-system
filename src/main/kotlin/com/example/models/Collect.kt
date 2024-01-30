package com.example.models

import com.example.util.Default
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Collect(
    val collectUserId: Long = Long.Default
)
