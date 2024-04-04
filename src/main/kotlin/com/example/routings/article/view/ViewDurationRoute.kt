package com.example.routings.article.view

import com.example.dao.view.duration.ViewDurationDao
import com.example.models.ViewDuration
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.util.Default
import com.example.util.viewArticleDurationPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * 用户浏览文章的时间，默认应该大于5s才上报
 */
fun Route.viewDurationRoute() {
    post(viewArticleDurationPath) {
        call.receive<ViewDurationTemp> {
            if (it.isValid()) {
                it.tags.forEach { tag ->
                    if (tag.isNotBlank()) {
                        ViewDurationDao.create(
                            ViewDuration(
                                userId = it.userId,
                                timestamp = it.timestamp,
                                duration = it.duration,
                                type = tag
                            )
                        )
                    }
                }
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>()
                )
            } else {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = DataResponse<Unit>()
                )
            }
        }
    }
}

@kotlinx.serialization.Serializable
data class ViewDurationTemp(
    val tags: List<String> = emptyList(),
    val userId: Long = Long.Default,
    val duration: Long = Long.Default,
    val timestamp: Long = Long.Default
) {
    fun isValid(): Boolean = userId != Long.Default && tags.isNotEmpty() && duration >= 5000
}