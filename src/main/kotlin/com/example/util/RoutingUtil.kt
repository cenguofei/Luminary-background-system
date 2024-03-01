package com.example.util

import com.example.models.Article
import com.example.models.responses.DataResponse
import com.example.plugins.security.noSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.invalidId(idName: String = "id") : Boolean {
    val id = parameters[idName]?.toLong()
    if (id == null || id < 0) {
        respond(
            status = HttpStatusCode.Conflict,
            message = DataResponse<Unit>().copy(msg = invalidId)
        )
        return true
    }
    return false
}

suspend fun ApplicationCall.noSessionAndInvalidId() : Boolean {
    if (noSession()) {
        return true
    }
    if (invalidId()) {
        return true
    }
    return false
}

suspend fun ApplicationCall.noSuchArticle(article: Article?) : Boolean {
    if (article == null) {
        respond(
            status = HttpStatusCode.Conflict,
            message = DataResponse<Unit>().copy(msg = noArticle)
        )
        return true
    }
    return false
}

suspend fun ApplicationCall.badRequest(
    predict: () -> Boolean = { true }
) : Boolean {
    if (predict()) {
        respond(
            status = HttpStatusCode.BadRequest,
            message = DataResponse<Unit>().copy(msg = HttpStatusCode.BadRequest.description)
        )
        return true
    }
    return false
}
