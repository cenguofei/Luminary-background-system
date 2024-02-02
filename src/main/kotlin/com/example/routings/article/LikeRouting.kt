package com.example.routings.article

import com.example.dao.article.LikeDao
import com.example.dao.article.LikeDaoImpl
import com.example.models.Like
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureLikeRouting() {
    val likeDao: LikeDao = LikeDaoImpl()
    routing {
        route(likeRootPath) {
            createLike(likeDao)
            deleteLike(likeDao)
            getAllLikesOfArticle(likeDao)
            getAllLikesOfUser(likeDao)
        }
    }
}

private fun Route.createLike(likeDao: LikeDao) {
    authenticate {
        post(createLikePath) {
            if (call.noSession()) {
                return@post
            }
            call.receive<Like> {
                if (it.userId != call.jwtUser?.id) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = DataResponse<Unit>(msg = internalErrorMsg)
                    )
                    return@post
                }
                likeDao.create(it)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>(success = true)
                )
            }
        }
    }
}

/**
 * ���»�õ����е���
 * ��Ҫ��������id
 */
private fun Route.getAllLikesOfArticle(likeDao: LikeDao) {
    get(getAllLikesOfArticlePath) {
        if (call.invalidId()) {
            return@get
        }
        val likes = likeDao.getAllLikesOfArticle(call.id)
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse(
                success = true,
                data = likes
            )
        )
    }
}

/**
 * �û������е���
 * ��Ҫ�����û���id
 */
private fun Route.getAllLikesOfUser(likeDao: LikeDao) {
    get(getAllLikesOfUserPath) {
        if (call.invalidId()) {
            return@get
        }
        val likes = likeDao.getAllLikesOfUser(call.id)
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse(
                success = true,
                data = likes
            )
        )
    }
}

/**
 * ������Ҫɾ����Like id
 */
private fun Route.deleteLike(likeDao: LikeDao) {
    authenticate {
        delete(deleteLikePath) {
            if (call.invalidId()) {
                return@delete
            }
            likeDao.delete(call.id)
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Unit>(
                    success = true,
                    msg = deleteSuccess
                )
            )
        }
    }
}

