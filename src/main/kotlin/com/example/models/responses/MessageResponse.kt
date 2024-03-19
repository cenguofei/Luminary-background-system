package com.example.models.responses

import com.example.models.*
import com.example.util.empty

/**
 * @param data list of [CombinedCommentMessage], [CombinedLikeMessage], [CombinedFollowMessage]
 */
@kotlinx.serialization.Serializable
class MessageResponse<L>: BaseResponse<L>() {
    fun copy(
        msg: String = empty,
        data: L? = null
    ): MessageResponse<L> {
        this.msg = msg
        this.data = data
        return this
    }
}

/**
 * @param messages type of [Like], [Comment], [Friend]
 */
@kotlinx.serialization.Serializable
data class CombinedMessage<T>(
    val user: User? = null,
    val article: Article? = null,
    val messages: List<T> = emptyList()
)

typealias CombinedCommentMessage = List<CombinedMessage<Comment>>
typealias CombinedLikeMessage = List<CombinedMessage<Like>>
typealias CombinedFollowMessage = List<CombinedMessage<Friend>>