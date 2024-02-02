package com.example.routings.article

import com.example.dao.article.CommentDao
import com.example.dao.article.CommentDaoImpl
import com.example.models.Comment
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

fun Application.configureCommentRouting() {
    val commentDao: CommentDao = CommentDaoImpl()
    routing {
        route(commentRootPath) {
            createComment(commentDao)
            deleteComment(commentDao)
            getAllCommentsOfArticle(commentDao)
            getAllCommentsOfUser(commentDao)
        }
    }
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
                        message = DataResponse<Unit>(msg = internalErrorMsg)
                    )
                    return@post
                }
                commentDao.create(it)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>(success = true)
                )
            }
        }
    }
}

/**
 * ������Ҫɾ����Collect id
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
                message = DataResponse<Unit>(
                    success = true,
                    msg = deleteSuccess
                )
            )
        }
    }
}


/**
 * ���»�õ����е���
 * ��Ҫ��������id
 */
private fun Route.getAllCommentsOfArticle(commentDao: CommentDao) {
    get(getAllCommentsOfArticlePath) {
        if (call.invalidId()) {
            return@get
        }
        val likes = commentDao.getAllCommentsOfArticle(call.id)
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
private fun Route.getAllCommentsOfUser(commentDao: CommentDao) {
    get(getAllCommentsOfUserPath) {
        if (call.invalidId()) {
            return@get
        }
        val comments = commentDao.getAllCommentsOfUser(call.id)
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse(
                success = true,
                data = comments
            )
        )
    }
}
