package com.example.routings.article

import com.example.dao.article.LikeDao
import com.example.dao.article.LikeDaoImpl
import com.example.models.Like
import com.example.models.responses.DataResponse
import com.example.models.responses.pagesData
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
            pageLikes(likeDao)
        }
    }
}

private fun Route.pageLikes(likeDao: LikeDao) {
    pagesData(
        dao = likeDao,
        requestPath = pageLikesPath
    )
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
                        message = DataResponse<Unit>().copy(msg = internalErrorMsg)
                    )
                    return@post
                }
                likeDao.create(it)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>()
                )
            }
        }
    }
}

/**
 * 文章获得的所有点赞
 * 需要传递文章id
 */
private fun Route.getAllLikesOfArticle(likeDao: LikeDao) {
    get(getAllLikesOfArticlePath) {
        if (call.invalidId()) {
            return@get
        }
        val likes = likeDao.getAllLikesOfArticle(call.id)
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<List<Like>>().copy(data = likes)
        )
    }
}

/**
 * 用户的所有点赞
 * 需要传递用户的id
 */
private fun Route.getAllLikesOfUser(likeDao: LikeDao) {
    get(getAllLikesOfUserPath) {
        if (call.invalidId()) {
            return@get
        }
        val likes = likeDao.getAllLikesOfUser(call.id)
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<List<Like>>().copy(data = likes)
        )
    }
}

/**
 * 传递需要删除的Like id
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
                message = DataResponse<Unit>().copy(msg = deleteSuccess)
            )
        }
    }
}

