package com.example.dao.message

import com.example.dao.LunimaryPage
import com.example.dao.article.ArticleDao
import com.example.dao.comment.CommentDao
import com.example.dao.user.UserDao
import com.example.models.Article
import com.example.models.Comment
import com.example.models.User
import com.example.models.ext.CommentItem
import com.example.models.ext.CommentItems
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class MessageDao(private val loginUser: User) : LunimaryPage<CommentItem> {
    override suspend fun pages(pageStart: Int, perPageCount: Int): List<CommentItem> {
        checkCombinedMessage()
        return if (message.isEmpty()) {
            emptyList()
        } else {
            message.page(pageStart, perPageCount)
        }
    }

    override suspend fun pageCount(): Long {
        checkCombinedMessage()
        return message.size.toLong()
    }

    private suspend fun checkCombinedMessage() {
        if (_messages == null) {
            getData(loginUser)
        }
    }

    private var _messages: CommentItems? = null
    private val message: CommentItems get() = _messages!!

    private suspend fun getData(user: User) {
        val result = mutableListOf<CommentTemp>()
        //1. 获取该用户评论过的文章
        val deffer1 = coroutineScope { async { articlesOfUserCommented(user) } }
        //2. 获取评论过该用户文章的信息
        val deffer2 = coroutineScope { async { commentsOfThisUser(user) } }
        val result1 = deffer1.await()
        val result2 = deffer2.await()
        println("message_for_comments result2=${result2.size}")
        println("message_for_comments result1=${result1.size}")
        result += result1
        result += result2
        println("message_for_comments result=${result.size}")
        if (result.isEmpty()) {
            _messages = listOf()
            return
        }
        val commentItems = result
            .map { tmp ->
                tmp.comments.map { comment ->
                    CommentItem(
                        user = tmp.user,
                        article = tmp.article,
                        comment = comment
                    )
                }
            }.flatten().sortedByDescending { it.comment.timestamp }
        this._messages = commentItems
        println("message_for_comments end")
    }
}

suspend fun articlesOfUserCommented(
    user: User
): List<CommentTemp> {
    val commentDao = CommentDao
    val articleDao: ArticleDao = ArticleDao
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
                CommentTemp(
                    user = user,
                    article = article,
                    comments = userCommented.filter { it.articleId == article.id }
                )
            }
        }
    }
}


suspend fun commentsOfThisUser(
    user: User
): List<CommentTemp> {
    val commentDao = CommentDao
    val articleDao: ArticleDao = ArticleDao
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
            val userDao = UserDao
            val needUsers = userDao.batchUsers(userIds)

            val needIdsOfArticle = needMessages.map { it.articleId }.toSet()
            val needArticles = articlesOfUser.filter { it.id in needIdsOfArticle }

            needArticles.map { article ->
                val userMessages = needMessages.filter { it.articleId == article.id }
                val oneMessage = userMessages[0]
                CommentTemp(
                    user = needUsers.find { it.id == oneMessage.userId }!!,
                    article = article,
                    comments = userMessages
                )
            }
        }
    }
}

/**
 * @param user 评论折
 * @param article [user]评论的文章
 * @param comments [user]对[article]的所有评论
 */
data class CommentTemp(
    val user: User,
    val article: Article,
    val comments: List<Comment>
)