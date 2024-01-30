package com.example.routings.file

import com.example.util.fileUrl
import com.example.util.resRoot
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.download() {
    get("/download") {
        val url = call.request.queryParameters[fileUrl]
        if (url.isNullOrEmpty()) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = "The url cannot be empty or null."
            )
            return@get
        }

        if (!url.startsWith(resRoot)) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = "The requested file address is illegal."
            )
            return@get
        }

        val file = File(url)
        if (file.isDirectory || !file.exists()) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = "URL cannot be a directory or file does not exist."
            )
            return@get
        }

        call.response.header(
            HttpHeaders.ContentDisposition,
            //以附件的方式下载
            ContentDisposition.Attachment.withParameter(
                key = ContentDisposition.Parameters.FileName,
                value = file.name
            ).toString()
        )
        call.respondFile(file)
    }
}