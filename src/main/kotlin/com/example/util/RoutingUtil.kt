package com.example.util

import com.example.models.Article
import com.example.models.responses.DataResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.invalidId() : Boolean {
    val id = parameters["id"]?.toLong()
    if (id == null) {
        respond(
            status = HttpStatusCode.Conflict,
            message = DataResponse<Unit>(message = invalidId)
        )
        return true
    }
    return false
}

suspend fun ApplicationCall.noSuchArticle(article: Article?) : Boolean {
    if (article == null) {
        respond(
            status = HttpStatusCode.Conflict,
            message = DataResponse<Unit>(message = noArticle)
        )
        return true
    }
    return false
}
