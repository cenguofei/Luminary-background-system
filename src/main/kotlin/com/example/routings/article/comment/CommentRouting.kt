package com.example.routings.article.comment

import com.example.dao.article.ArticleDao
import com.example.dao.comment.CommentDao
import com.example.dao.user.UserDao
import com.example.models.Comment
import com.example.models.ext.CommentWithUser
import com.example.models.responses.DataResponse
import com.example.models.responses.PageOptions
import com.example.models.responses.pagesData
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureCommentRouting() {
    val commentDao = CommentDao
    routing {
        route(commentRootPath) {
            createCommentRoute(commentDao)
            deleteComment(commentDao)
            getAllCommentsOfArticle(commentDao)
            getAllCommentsOfUser(commentDao)
            pageComments(commentDao)
        }
    }
}

private fun Route.pageComments(commentDao: CommentDao) {
    pagesData<Comment>(
        pageOptions = PageOptions { commentDao },
        requestPath = pageCommentsPath
    )
}

/**
 * 传递需要删除的Collect id
 */
private fun Route.deleteComment(commentDao: CommentDao) {
    authenticate {
        delete(deleteCommentPath) {
            if (call.invalidId<Boolean>()) {
                return@delete
            }
            val commentID = call.id
            val readComment = CommentDao.read(commentID)
            if (readComment == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = DataResponse<Boolean>().copy(
                        msg = "The comment ID: $commentID not exist."
                    )
                )
                return@delete
            }

            val user = call.jwtUser!!
            var shouldDelete = false
            if (readComment.id == user.id) {
                shouldDelete = true
            } else {
                val userArticlesOnlyId = ArticleDao.userArticlesOnlyId(user.id)
                if (readComment.articleId in userArticlesOnlyId) {
                    shouldDelete = true
                }
            }
            if (!shouldDelete) {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = DataResponse<Boolean>().copy(
                        msg = "You cannot delete this comment."
                    )
                )
                return@delete
            }
            commentDao.delete(commentID)
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Boolean>().copy(
                    msg = deleteSuccess,
                    data = true
                )
            )
        }
    }
}


/**
 * 文章获得的所有评论
 * 需要传递文章id
 */
private fun Route.getAllCommentsOfArticle(commentDao: CommentDao) {
    get(getAllCommentsOfArticlePath) {
        if (call.invalidId<List<CommentWithUser>>("articleId")) {
            return@get
        }
        val comments = commentDao.getAllCommentsOfArticle(call.id("articleId"))
        if (comments.isEmpty()) {
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<List<CommentWithUser>>().copy(
                    msg = "No comments of this article."
                )
            )
        }
        val userDao = UserDao
        val users = userDao.batchUsers(comments.map { it.userId })
        val result = users.map { user ->
            val userComments = comments.filter { it.userId == user.id }
                .sortedBy { it.timestamp }
            CommentWithUser(
                user = user,
                comments = userComments
            )
        }
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<List<CommentWithUser>>().copy(data = result)
        )
    }
}

/**
 * 用户的所有评论
 * 需要传递用户的id
 */
private fun Route.getAllCommentsOfUser(commentDao: CommentDao) {
    get(getAllCommentsOfUserPath) {
        if (call.invalidId<List<Comment>>()) {
            return@get
        }
        val comments = commentDao.getAllCommentsOfUserCommentToArticle(call.id)
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<List<Comment>>().copy(data = comments)
        )
    }
}
