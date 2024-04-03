package com.example.routings.user.interactiondata

import com.example.models.ext.InteractionData
import com.example.models.responses.DataResponse
import com.example.util.interactionDataPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.interactionDataRoute() {
    get(interactionDataPath) {
        val loginUserId = call.parameters["loginUserId"]?.toLongOrNull()
        if (loginUserId == null) {
            call.respond(
                status = HttpStatusCode.Conflict,
                message = DataResponse<InteractionData>().copy(
                    msg = "User ID cannot be null."
                )
            )
            return@get
        }
        val interactionData = Interaction.interactionData(loginUserId)
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<InteractionData>().copy(
                data = interactionData
            )
        )
    }
}