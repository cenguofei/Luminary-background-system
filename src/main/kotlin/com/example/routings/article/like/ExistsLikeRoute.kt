package com.example.routings.article.like

import com.example.dao.article.LikeDao
import com.example.models.Like
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.util.existsLikePath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.existsLikeRoute(likeDao: LikeDao) {
    post (existsLikePath) {
        call.receive<Like> {
            val exists = likeDao.exists(it)
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Boolean>().copy(
                    data = exists
                )
            )
        }
    }
}