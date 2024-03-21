package com.example.routings.article.message

import com.example.dao.article.mapToArticle
import com.example.models.*
import com.example.models.responses.DataResponse
import com.example.models.tables.Articles
import com.example.models.tables.DELETED_ARTICLE_ID
import com.example.models.tables.Likes
import com.example.models.tables.Users
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.dbTransaction
import com.example.util.messageLikePath
import com.example.util.unknownErrorMsg
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.selectAll

/**
 * 需要的信息：
 * 谁点赞了谁（loginUser）的文章
 * 1. 点赞人
 * 2. 文章
 *
 * 关系： 点赞人 to 文章
 */
fun Route.messageForLike() {
    authenticate {
        get(messageLikePath) {
            if (call.noSession<LikeMessageResponse>()) {
                return@get
            }
            // 1. jwt.jwtUser 拿到登录用户 id
            val loginUserId = call.jwtUser?.id ?: run {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = DataResponse<LikeMessageResponse>().copy(msg = unknownErrorMsg)
                )
                return@get
            }

            // 2. 查询 id 的获赞的文章 userArticles
            val likedArticles = dbTransaction {
                Articles.innerJoin(
                    otherTable = Likes,
                    onColumn = {
                        this.id
                    },
                    otherColumn = {
                        this.articleId
                    },
                    additionalConstraint = {
                        Articles.userId eq loginUserId
                    }
                ).selectAll().mapToArticle()
            }

            val likedArticlesId = likedArticles.map { it.id }
            val temps = dbTransaction {
                Users.innerJoin(
                    otherTable = Likes,
                    onColumn = {
                        this.id
                    },
                    otherColumn = {
                        this.userId
                    },
                    additionalConstraint = {
                        (Likes.articleId inList likedArticlesId) and (Users.id neq loginUserId)
                    }
                ).selectAll().map {
                    Temp(
                        user = User(
                            username = it[Users.username],
                            age = it[Users.age],
                            sex = Sex.valueOf(it[Users.sex]),
                            id = it[Users.id],
                            headUrl = it[Users.headUrl],
                            background = it[Users.background],
                            role = Role.valueOf(it[Users.role]),
                            status = UserStatus.valueOf(it[Users.status]),
                            password = it[Users.password]
                        ),
                        like = Like(
                            id = it[Likes.id],
                            userId = it[Likes.userId],
                            articleId = it[Likes.articleId] ?: DELETED_ARTICLE_ID,
                            timestamp = it[Likes.timestamp]
                        )
                    )
                }
            }

            val likeMessages = temps.map {
                val user = it.user
                val like = it.like
                val find = likedArticles.find { a -> a.id == like.articleId }
                LikeMessage(
                    likeUser = user,
                    article = find!!,
                    timestamp = like.timestamp
                )
            }

            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<List<LikeMessage>>().copy(
                    data = likeMessages,
                    msg = "${likeMessages.size} data respond."
                )
            )
        }
    }
}

data class Temp(
    val user: User,
    val like: Like
)

@kotlinx.serialization.Serializable
data class LikeMessage(
    val likeUser: User, //点赞人
    val article: Article, //点赞的文章
    val timestamp: Long
)

typealias LikeMessageResponse = DataResponse<List<LikeMessage>>