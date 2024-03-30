package com.example.routings.article.message

import com.example.dao.message.MessageDao
import com.example.models.ext.CommentItem
import com.example.models.responses.PageOptions
import com.example.models.responses.pagesData
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.isNull
import com.example.util.messageCommentPath
import io.ktor.server.auth.*
import io.ktor.server.routing.*

/**
 * 需要传递参数：
 * 1. token
 * 2. session
 */
fun Route.messageForComments() {
    authenticate {
        pagesData<CommentItem>(
            requestPath = messageCommentPath,
            pageOptions = PageOptions(
                onIntercept = {
                    it.jwtUser.isNull || it.noSession
                },
                onCreateDao = { MessageDao(it.jwtUser!!) },
            )
        )
    }
}