package com.example.routings.article.message

import com.example.dao.article.ArticleDao
import com.example.dao.article.CommentDao
import com.example.dao.user.UserDao
import com.example.models.Comment
import com.example.models.User
import com.example.models.responses.CombinedCommentMessage
import com.example.models.responses.CombinedMessage
import com.example.models.responses.MessageResponse
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.messageCommentPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async

/**
 * 需要传递参数：
 * 1. token
 * 2. session
 */
fun Route.messageForComments() {
    authenticate {
        get(messageCommentPath) {
            val user = call.jwtUser
            if (user == null) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = MessageResponse<CombinedCommentMessage>().copy(
                        msg = "服务器错误，请稍后再试！"
                    )
                )
                return@get
            }
            if (call.noSession<CombinedCommentMessage>()) {
                return@get
            }
            val result = mutableListOf<CombinedMessage<Comment>>()
            //1. 获取该用户评论过的文章
            val deffer1 = async { articlesOfUserCommented(user) }
            //2. 获取评论过该用户文章的信息
            val deffer2 = async { commentsOfThisArticle(user) }
            val result1 = deffer1.await()
            val result2 = deffer2.await()
            println("message_for_comments result2=${result2.size}")
            println("message_for_comments result1=${result1.size}")
            result += result1
            result += result2
            println("message_for_comments result=${result.size}")
            if (result.isEmpty()) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = MessageResponse<CombinedCommentMessage>().copy(
                        msg = "还没有评论哦，快去写作或者评论他人的文章吧！"
                    )
                )
                return@get
            }
            call.respond(
                status = HttpStatusCode.OK,
                message = MessageResponse<CombinedCommentMessage>().copy(
                    data = result,
                    msg = "query success with ${result.size} comments."
                )
            )
            println("message_for_comments end")
        }
    }
}

private suspend fun articlesOfUserCommented(
    user: User
): CombinedCommentMessage {
    val commentDao: CommentDao = CommentDao.Default
    val articleDao: ArticleDao = ArticleDao.Default
    println("message_for_comments start query")
    val userCommented = commentDao.getAllCommentsOfUserCommentToArticle(user.id)
    println("message_for_comments userCommented=${userCommented.map { it.id }}")
    val articlesOfUserCommented = when {
        userCommented.isEmpty() -> emptyList()
        else -> {
            val articleIds = userCommented.map { it.articleId }.toSet().toList()
            articleDao.getArticlesByIds(articleIds)
        }
    }
    println("message_for_comments articlesOfUserCommented=${articlesOfUserCommented.map { it.id }}")
    return when {
        articlesOfUserCommented.isEmpty() -> emptyList()
        else -> {
            articlesOfUserCommented.map { article ->
                CombinedMessage(
                    user = user,
                    article = article,
                    messages = userCommented.filter { it.articleId == article.id }
                )
            }
        }
    }
}

private suspend fun commentsOfThisArticle(
    user: User
): CombinedCommentMessage {
    val commentDao: CommentDao = CommentDao.Default
    val articleDao: ArticleDao = ArticleDao.Default
    val articlesOfUser = articleDao.getArticlesOfUser(user.id)
    println("message_for_comments articlesOfUser=${articlesOfUser.map { it.id }}")
    val commentsOfUserArticleWhichGetComment = when {
        articlesOfUser.isEmpty() -> emptyList()
        else -> {
            commentDao.getCommentsByIdsOfArticle(articlesOfUser.map { it.id })
        }
    }
    println("message_for_comments commentsOfUserArticleWhichGetComment=${commentsOfUserArticleWhichGetComment.map { it.id }}")

    return when {
        commentsOfUserArticleWhichGetComment.isEmpty() -> {
            emptyList()
        }
        else -> {
            val needMessages = commentsOfUserArticleWhichGetComment
                .filter { it.userId != user.id } // result1中已经包含

            val userIds = needMessages.map { it.userId }
            val userDao: UserDao = UserDao.Default
            val needUsers = userDao.batchUsers(userIds)

            val needIdsOfArticle = needMessages.map { it.articleId }.toSet()
            val needArticles = articlesOfUser.filter { it.id in needIdsOfArticle }

            needArticles.map { article ->
                val userMessages = needMessages.filter { it.articleId == article.id }
                val oneMessage = userMessages[0]
                CombinedMessage(
                    user = needUsers.find { it.id == oneMessage.userId },
                    article = article,
                    messages = userMessages
                )
            }
        }
    }
}