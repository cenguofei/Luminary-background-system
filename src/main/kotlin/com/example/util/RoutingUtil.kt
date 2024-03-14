package com.example.util

import com.example.models.Article
import com.example.models.responses.DataResponse
import com.example.plugins.security.noSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun <T> ApplicationCall.invalidId(idName: String = "id") : Boolean {
    val id = parameters[idName]?.toLong()
    if (id == null || id < 0) {
        respond(
            status = HttpStatusCode.Conflict,
            message = DataResponse<T>().copy(msg = invalidId)
        )
        return true
    }
    return false
}

suspend fun <T> ApplicationCall.noSessionAndInvalidId() : Boolean {
    if (noSession<T>()) {
        return true
    }
    if (invalidId<T>()) {
        return true
    }
    return false
}

suspend fun <T> ApplicationCall.noSuchArticle(article: Article?) : Boolean {
    if (article == null) {
        respond(
            status = HttpStatusCode.Conflict,
            message = DataResponse<T>().copy(msg = noArticle)
        )
        return true
    }
    return false
}

suspend fun <T> ApplicationCall.badRequest(
    predict: () -> Boolean = { true }
) : Boolean {
    if (predict()) {
        respond(
            status = HttpStatusCode.BadRequest,
            message = DataResponse<T>().copy(msg = HttpStatusCode.BadRequest.description)
        )
        return true
    }
    return false
}
