package com.example.routings.article.like

import com.example.dao.like.LikeDao
import com.example.models.Like
import com.example.models.responses.DataResponse
import com.example.models.responses.PageOptions
import com.example.models.responses.pagesData
import com.example.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureLikeRouting() {
    val likeDao = LikeDao
    routing {
        route(likeRootPath) {
            giveALikeRoute(likeDao)
            cancelLikeRoute(likeDao)
            deleteLike()
            getAllLikesOfArticle(likeDao)
            getAllLikesOfUser(likeDao)
            pageLikes(likeDao)
            existsLikeRoute(likeDao)
        }
    }
}

private fun Route.pageLikes(likeDao: LikeDao) {
    pagesData<Like>(
        pageOptions = PageOptions { likeDao },
        requestPath = pageLikesPath
    )
}

/**
 * 文章获得的所有点赞
 * 需要传递文章id
 */
private fun Route.getAllLikesOfArticle(likeDao: LikeDao) {
    get(getAllLikesOfArticlePath) {
        if (call.invalidId<List<Like>>()) {
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
        if (call.invalidId<List<Like>>()) {
            return@get
        }
        val likes = likeDao.getAllLikesOfUser(call.id)
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<List<Like>>().copy(data = likes)
        )
    }
}
