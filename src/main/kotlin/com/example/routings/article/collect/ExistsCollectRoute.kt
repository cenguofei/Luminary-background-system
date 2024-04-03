package com.example.routings.article.collect

import com.example.dao.article.CollectDao
import com.example.models.Collect
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.util.existsCollectPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.existsCollectRoute(collectDao: CollectDao) {
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