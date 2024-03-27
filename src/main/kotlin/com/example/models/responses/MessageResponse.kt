package com.example.models.responses

import com.example.models.*
import com.example.util.empty

/**
 * @param data list of [CombinedCommentMessage], [CombinedLikeMessage], [CombinedFollowMessage]
 */
@kotlinx.serialization.Serializable
class MessageResponse<L>: BaseResponse<L>()

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
typealias CombinedFollowMessage = List<CombinedMessage<Friend>>