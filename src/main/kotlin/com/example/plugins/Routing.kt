package com.example.plugins

import com.example.models.responses.DataResponse
import com.example.routings.article.configureArticleRouting
import com.example.routings.article.configureCollectRouting
import com.example.routings.article.configureCommentRouting
import com.example.routings.article.configureLikeRouting
import com.example.routings.file.configureFileRouting
import com.example.routings.user.configureUserRouting
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.reflect.*

fun Application.configureRouting() {
    configureUserRouting()
    configureFileRouting()
    configureArticleRouting()
    configureLikeRouting()
    configureCollectRouting()
    configureCommentRouting()
}

suspend inline fun <reified T : Any> ApplicationCall.receive(
    processError: Boolean = true,
    errorMessage: Any? = null,
    onError: (CannotTransformContentToTypeException) -> Unit = {},
    onSuccess: (T) -> Unit
) {
    val data = receiveNullable<T>(typeInfo<T>())
    if (data != null) {
        onSuccess(data)
    } else {
        val exception = CannotTransformContentToTypeException(typeInfo<T>().kotlinType!!)
        if (processError) {
            respond(
                status = HttpStatusCode.InternalServerError,
                message = errorMessage ?: DataResponse<Unit>(
                    msg = HttpStatusCode.InternalServerError.description
                )
            )
        } else {
            onError(exception)
        }
    }
}