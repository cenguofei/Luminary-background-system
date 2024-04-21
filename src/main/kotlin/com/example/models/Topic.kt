package com.example.models

import com.example.util.Default
import com.example.util.empty

/**
 * ��������
 */
@kotlinx.serialization.Serializable
data class Topic(
    val id: Long = Long.Default,

    val topic: String = empty,

    /**
     * �������ܻ�ӭ�̶�
     */
    val fashion: Int = Int.Default,

    val timestamp: Long = Long.Default
)

@kotlinx.serialization.Serializable
data class UserSelectedTopics(
    val userId: Long = Long.Default,

    /**
     * array of [Topic.id]
     */
    val topics: Array<Long> = emptyArray()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserSelectedTopics

        if (userId != other.userId) return false
        if (!topics.contentEquals(other.topics)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + topics.contentHashCode()
        return result
    }
}