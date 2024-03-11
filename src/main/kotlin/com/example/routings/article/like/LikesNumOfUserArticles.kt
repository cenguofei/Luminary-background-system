package com.example.routings.article.like

import com.example.dao.article.ArticleDao
import com.example.dao.article.ArticleDaoImpl
import com.example.dao.article.LikeDao
import com.example.models.responses.DataResponse
import com.example.util.likesOfUserPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.likesNumOfUser(likeDao: LikeDao) {
    val articleDao: ArticleDao = ArticleDaoImpl()
    get(likesOfUserPath) {
        val userId = call.parameters["userId"]?.toLongOrNull()
        if (userId == null) {
            call.respond(
                status = HttpStatusCode.Conflict,
                message = DataResponse<Long>().copy(
                    msg = "ÓÃ»§idÎª¿Õ.",
                    data = -1
                )
            )
            return@get
        }
        val articles = articleDao.getArticlesOfUser(userId)
        if (articles.isEmpty()) {
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Long>().copy(
                    data = 0
                )
            )
            return@get
        }
        val likesNum = likeDao.likesNumOfUserArticles(userId, articles.map { it.id })
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<Long>().copy(
                data = likesNum
            )
        )
    }
}