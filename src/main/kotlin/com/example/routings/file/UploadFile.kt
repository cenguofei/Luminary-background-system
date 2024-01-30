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

/**
 * 上传图片，如果成功则返回图片地址
 */
fun Route.upload(userDao: UserDao) {
    authenticate {
        post("/file/upload") {
            if (call.noSession()) {
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
                        DataResponse<Unit>(message = faild.second, success = false)
                    )
                )
                deleteCreatedFiles(filenames)
                return@post
            }

            val queryUser = userDao.readByUsername(sessionUser.username)?.copy(headUrl = directory)
            if (queryUser != null) {
                userDao.updateByUsername(sessionUser.username, queryUser)
            } else {
                call.respond(
                    DataResponse<Unit>(
                        message = unknownErrorMsg,
                        success = false
                    )
                )
                return@post
            }

            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse(
                    message = "Successfully uploaded image, the new filename is $directory",
                    success = true,
                    data = directory
                )
            )
        }
    }
}

fun getFilename(originName: String) = System.currentTimeMillis().toString() + originName

fun deleteCreatedFiles(filenames: List<String>) {
    filenames.forEach { File(it).delete() }
}