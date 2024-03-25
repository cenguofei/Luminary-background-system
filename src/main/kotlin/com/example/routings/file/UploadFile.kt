package com.example.routings.file

import com.example.dao.user.UserDao
import com.example.models.responses.DataResponse
import com.example.plugins.security.noSession
import com.example.plugins.security.sessionUser
import com.example.util.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

/**
 * 上传图片，如果成功则返回图片地址
 */
fun Route.upload(userDao: UserDao) {
    authenticate {
        post("/upload") {
            if (call.noSession<UploadData>()) {
                return@post
            }

            val uploadType = call.request.queryParameters["upload_type"]?.toIntOrNull()
            if (uploadType == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = DataResponse<UploadData>().copy(
                        msg = "Please specify the type of file you want to upload."
                    )
                )
                return@post
            }

            if (uploadType !in uploadTypes) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = DataResponse<UploadData>().copy(
                        msg = "Invalid request type."
                    )
                )
                return@post
            }

            val sessionUser = call.sessionUser!!
            var fileDescription = empty
            val directory = "${resRoot}/uploads/${sessionUser.username}/"

            File(directory).let {
                if (!it.exists()) { it.mkdirs() }
            }

            val multipartData = call.receiveMultipart()
            var faild = false to empty
            val filenames = mutableListOf<String>()

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        fileDescription = part.value
                    }

                    is PartData.FileItem -> {
                        try {
                            val filename = directory + getFilename(part.originalFileName.toString())
                            val fileBytes = part.streamProvider().readBytes()
                            val newFile = File(filename)
                            if (withContext(Dispatchers.IO) { newFile.createNewFile() }) {
                                newFile.writeBytes(fileBytes)
                                filenames += filename
                            } else {
                                faild = true to serverError
                                return@forEachPart
                            }
                        } catch (e: Exception) {
                            faild = true to e.message.toString()
                            return@forEachPart
                        }
                    }

                    else -> {
                        part.toString().logd("upload_file")
                    }
                }
                part.dispose()
            }

            if (faild.first) {
                call.respond(
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        DataResponse<UploadData>().copy(msg = faild.second)
                    )
                )
                deleteCreatedFiles(filenames)
                return@post
            }

            if (filenames.isEmpty()) {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = DataResponse<UploadData>().copy(
                        msg = "Please ensure that you have uploaded the file."
                    )
                )
                return@post
            }

            when(uploadType) {
                UPLOAD_TYPE_USER_HEAD, UPLOAD_TYPE_USER_BACKGROUND -> {
                    val queryUser = userDao.readByUsername(sessionUser.username)
                    if (queryUser != null) {
                        val newUser = if (uploadType == UPLOAD_TYPE_USER_HEAD) {
                            queryUser.copy(headUrl = filenames[0])
                        } else queryUser.copy(background = filenames[0])
                        userDao.updateByUsername(sessionUser.username, newUser)
                    } else {
                        call.respond(
                            DataResponse<UploadData>().copy(msg = unknownErrorMsg)
                        )
                        return@post
                    }
                }
                UPLOAD_TYPE_ARTICLE_COVER -> { }
                UPLOAD_TYPE_OTHER -> { }
            }
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<UploadData>().copy(
                    msg = "Successfully uploaded image, the new filename is $directory${filenames.toTypedArray()}",
                    data = UploadData(filenames = filenames)
                )
            )
        }
    }
}

fun getFilename(originName: String) = UUID.randomUUID().toString() + "_"+ originName

fun deleteCreatedFiles(filenames: List<String>) {
    filenames.forEach { File(it).delete() }
}