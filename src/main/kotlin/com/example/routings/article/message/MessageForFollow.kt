package com.example.routings.article.message

import com.example.dao.message.FollowPageDao
import com.example.models.ext.UserFriend
import com.example.models.responses.PageOptions
import com.example.models.responses.pagesData
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.isNull
import com.example.util.messageFollowPath
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.messageForFollow() {
    authenticate {
        pagesData<UserFriend>(
            requestPath = messageFollowPath,
            pageOptions = PageOptions(
                onIntercept = {
                    it.noSession || it.jwtUser.isNull
                },
                onCreateDao = { FollowPageDao(it.jwtUser!!.id) }
            )
        )
    }
}