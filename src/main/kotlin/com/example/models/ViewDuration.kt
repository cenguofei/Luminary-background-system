package com.example.models

import com.example.util.Default
import com.example.util.empty

/**
 * �û��������ʱ���࣬�����Ƽ�����
 */
@kotlinx.serialization.Serializable
data class ViewDuration(
    val id: Long = Long.Default,

    val userId: Long = Long.Default,

    val type: String = empty,

    val duration: Long = Long.Default,

    val timestamp: Long = Long.Default
)