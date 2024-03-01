package com.example.routings.article

import com.example.dao.article.CommentDao
import com.example.dao.article.CommentDaoImpl
import com.example.models.Comment
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

fun Application.configureCommentRouting() {
    val commentDao: CommentDao = CommentDaoImpl()
    routing {
        route(commentRootPath) {
            createComment(commentDao)
            deleteComment(commentDao)
            getAllCommentsOfArticle(commentDao)
            getAllCommentsOfUser(commentDao)
            pageComments(commentDao)
        }
    }
}

private fun Route.pageComments(commentDao: CommentDao) {
    pagesData(
        dao = commentDao,
        requestPath = pageCommentsPath
    )
}

private fun Route.createComment(commentDao: CommentDao) {
    authenticate {
        post(createCommentPath) {
            if (call.noSession()) {
                return@post
            }
            call.receive<Comment> {
                if (it.userId != call.jwtUser?.id) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = DataResponse<Unit>().copy(msg = internalErrorMsg)
                    )
                    return@post
                }
                commentDao.create(it)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>()
                )
            }
        }
    }
}

/**
 * 传递需要删除的Collect id
 */
private fun Route.deleteComment(commentDao: CommentDao) {
    authenticate {
        delete(deleteCommentPath) {
            if (call.invalidId()) {
                return@delete
            }
            commentDao.delete(call.id)
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Unit>().copy(msg = deleteSuccess)
            )
        }
    }
}


/**
 * 文章获得的所有点赞
 * 需要传递文章id
 */
private fun Route.getAllCommentsOfArticle(commentDao: CommentDao) {
    get(getAllCommentsOfArticlePath) {
        if (call.invalidId()) {
            return@get
        }
        val comments = commentDao.getAllCommentsOfArticle(call.id)
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<List<Comment>>().copy(data = comments)
        )
    }
}

/**
 * 用户的所有点赞
 * 需要传递用户的id
 */
private fun Route.getAllCommentsOfUser(commentDao: CommentDao) {
    get(getAllCommentsOfUserPath) {
        if (call.invalidId()) {
            return@get
        }
        val comments = commentDao.getAllCommentsOfUser(call.id)
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<List<Comment>>().copy(data = comments)
        )
    }
}
