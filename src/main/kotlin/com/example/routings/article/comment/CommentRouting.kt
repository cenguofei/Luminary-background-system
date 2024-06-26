package com.example.routings.article.comment

import com.example.dao.article.ArticleDao
import com.example.dao.comment.CommentDao
import com.example.dao.user.UserDao
import com.example.models.Comment
import com.example.models.ext.CommentWithUser
import com.example.models.responses.DataResponse
import com.example.plugins.security.jwtUser
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
        }
    }
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
            var shouldInvisibleToTheUser = false
            if (readComment.userId == user.id) {
                shouldDelete = true
            } else {
                val userArticlesOnlyId = ArticleDao.userArticlesOnlyId(user.id)
                if (readComment.articleId in userArticlesOnlyId) {
                    shouldInvisibleToTheUser = true
                }
            }
            if (shouldDelete) {
                commentDao.delete(commentID)
            }
            if (shouldInvisibleToTheUser) {
                commentDao.update(readComment.copy(visibleToOwner = false))
            }

            if (!shouldDelete && !shouldInvisibleToTheUser) {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = "You cannot delete this comment."
                )
                return@delete
            }

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
