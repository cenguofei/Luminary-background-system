package com.example.routings.article.collect

import com.example.dao.article.CollectDao
import com.example.dao.article.LikeDao
import com.example.models.Collect
import com.example.models.Like
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.util.existsCollectPath
import com.example.util.existsLikePath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.existsCollect(collectDao: CollectDao) {
    post (existsCollectPath) {
        call.receive<Collect> {
            val exists = collectDao.exists(it)
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Boolean>().copy(
                    data = exists
                )
            )
        }
    }
}