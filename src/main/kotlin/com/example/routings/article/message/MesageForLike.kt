package com.example.routings.article.message

import com.example.dao.message.LikeMessage
import com.example.dao.message.LikePageDao
import com.example.models.responses.PageOptions
import com.example.models.responses.pagesData
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.isNull
import com.example.util.messageLikePath
import io.ktor.server.auth.*
import io.ktor.server.routing.*

/**
 * 需要的信息：
 * 谁点赞了谁（loginUser）的文章
 * 1. 点赞人
 * 2. 文章
 *
 * 关系： 点赞人 to 文章
 */
fun Route.messageForLike() {
    authenticate {
        pagesData<LikeMessage>(
            requestPath = messageLikePath,
            pageOptions = PageOptions(
                onIntercept = {
                    it.noSession || it.jwtUser.isNull
                },
                onCreateDao = { LikePageDao(it.jwtUser!!.id) }
            )
        )
    }
}