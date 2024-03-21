package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.dao.article.CollectDao
import com.example.dao.article.LikeDao
import com.example.dao.user.UserDao
import com.example.models.Article
import com.example.models.Like
import com.example.models.responses.DataResponse
import com.example.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * 用户点赞过的文章
 * 需要传递用户的id
 */
fun Route.getAllArticlesOfUserLiked() {
    val likeDao = LikeDao
    allArticlesOfUserBy(
        path = getAllArticlesOfUserLikedPath,
        articleIds = { userId ->
            likeDao.getAllLikesOfUser(userId).map { it.articleId }
        },
        type = ArticlesOfUserType.Liked
    )
}

/**
 * 用户收藏的文章
 * 需要传递用户的id
 */
fun Route.getAllArticlesOfUserCollected() {
    val collectDao = CollectDao
    allArticlesOfUserBy(
        path = getAllArticlesOfUserCollectedPath,
        articleIds = { userId ->
            collectDao.getAllCollectsOfUser(userId).map { it.articleId }
        },
        type = ArticlesOfUserType.Collected
    )
}

private fun Route.allArticlesOfUserBy(
    path: String,
    articleIds: suspend (userId: Long) -> List<Long>,
    type: ArticlesOfUserType
) {
    val articleDao = ArticleDao
    val userDao = UserDao
    get(path) {
        if (call.invalidId<List<Article>>("userId")) {
            return@get
        }
        if (!userDao.existing(call.id("userId"))) {
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = DataResponse<List<Article>>().copy(
                    msg = "User does not exist!"
                )
            )
            return@get
        }

        val ids = articleIds(call.id("userId"))
        if (ids.isEmpty()) {
            val msg = when (type) {
                ArticlesOfUserType.Collected -> {
                    "You haven't collected any articles yet, go and collect them!"
                }
                ArticlesOfUserType.Liked -> {
                    "You haven't liked the article yet, go like it now!"
                }
            }
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<List<Article>>().copy(
                    msg = msg
                )
            )
            return@get
        }
        val articles = articleDao.getArticlesByIds(ids)
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<List<Article>>().copy(data = articles)
        )
    }
}

enum class ArticlesOfUserType {
    Collected,
    Liked
}