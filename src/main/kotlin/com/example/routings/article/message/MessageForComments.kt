package com.example.routings.article.message

import com.example.dao.article.ArticleDao
import com.example.dao.article.CommentDao
import com.example.models.Article
import com.example.models.Comment
import com.example.models.responses.CombinedCommentMessage
import com.example.models.responses.CombinedMessage
import com.example.models.responses.MessageResponse
import com.example.plugins.messages
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.empty
import com.example.util.messageCommentPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * ��Ҫ���ݲ�����
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
                        msg = "�������������Ժ����ԣ�"
                    )
                )
                return@get
            }
            if (call.noSession<CombinedCommentMessage>()) {
                return@get
            }
            val result = mutableListOf<CombinedMessage<Comment>>()
            //1. ��ȡ���û����۹�������
            val commentDao: CommentDao = CommentDao.Default
            val articleDao: ArticleDao = ArticleDao.Default
            println("message_for_comments start query")
            val userCommented = commentDao.getAllCommentsOfUserCommentToArticle(user.id)
            println("message_for_comments userCommented=$userCommented")
            val articlesOfUserCommented = when {
                userCommented.isEmpty() -> emptyList()
                else -> {
                    val articleIds = userCommented.map { it.articleId }.toSet().toList()
                    articleDao.getArticlesByIds(articleIds)
                }
            }
            println("message_for_comments articlesOfUserCommented=$articlesOfUserCommented")
            val result1 = when {
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
            println("message_for_comments result1=$result1")
            if (result1.isNotEmpty()) {
                result += result1
            }

            //2. ��ȡ���۹����û����µ���Ϣ
            val articlesOfUser = articleDao.getArticlesOfUser(user.id)
            println("message_for_comments articlesOfUser=${articlesOfUser.map { it.id }}")
            val commentsOfUserArticleWhichGetComment = when {
                articlesOfUser.isEmpty() -> emptyList()
                else -> {
                    commentDao.getCommentsByIdsOfArticle(articlesOfUser.map { it.id })
                }
            }
            println("message_for_comments commentsOfUserArticleWhichGetComment=$commentsOfUserArticleWhichGetComment")

            val result2 = when {
                commentsOfUserArticleWhichGetComment.isEmpty() -> {
                    emptyList()
                }
                else -> {
                    val ids = commentsOfUserArticleWhichGetComment
                        .filter { it.userId != user.id } // result1���Ѿ�����
                        .map { it.articleId }.toSet()
                    articlesOfUser.filter { it.id in ids }
                        .map { article ->
                            CombinedMessage(
                                user = user,
                                article = article,
                                messages = commentsOfUserArticleWhichGetComment.filter { it.articleId == article.id }
                            )
                        }
                }
            }
            println("message_for_comments result2=$result2")
            if (result2.isNotEmpty()) {
                result += result2
            }
            println("message_for_comments result=$result")
            if (result.isEmpty()) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = MessageResponse<CombinedCommentMessage>().copy(
                        msg = "��û������Ŷ����ȥд�������������˵����°ɣ�"
                    )
                )
                return@get
            }
            println("message_for_comments end")
            call.respond(
                status = HttpStatusCode.OK,
                message = MessageResponse<CombinedCommentMessage>().copy(
                    data = result
                )
            )
        }
    }
}