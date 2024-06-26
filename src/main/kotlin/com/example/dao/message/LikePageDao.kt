package com.example.dao.message

import com.example.dao.LunimaryPage
import com.example.dao.article.mapToArticle
import com.example.dao.user.mapRowToUser
import com.example.models.Article
import com.example.models.Like
import com.example.models.User
import com.example.models.tables.Articles
import com.example.models.tables.DELETED_ARTICLE_ID
import com.example.models.tables.Likes
import com.example.models.tables.Users
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.selectAll

class LikePageDao(
    private val loginUserId: Long
) : LunimaryPage<LikeMessage> {
    override suspend fun pages(pageStart: Int, perPageCount: Int): List<LikeMessage> {
        check()
        return likeMessages.page(pageStart, perPageCount)
    }

    override suspend fun pageCount(): Long {
        check()
        return likeMessages.size.toLong()
    }

    private var _likeMessages: List<LikeMessage>? = null
    private val likeMessages: List<LikeMessage> get() = _likeMessages!!

    private suspend fun check() {
        if (_likeMessages == null) {
            getData()
        }
    }

    private suspend fun getData() {
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
                    (Articles.userId eq loginUserId) and (Likes.visibleToOwner eq true)
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
                    (Users.id neq loginUserId).and(Likes.articleId inList likedArticlesId)
                        .and(Likes.visibleToOwner eq true)
                }
            ).selectAll().map {
                Temp(
                    user = it.mapRowToUser(),
                    like = Like(
                        id = it[Likes.id],
                        userId = it[Likes.userId],
                        articleId = it[Likes.articleId] ?: DELETED_ARTICLE_ID,
                        timestamp = it[Likes.timestamp],
                        visibleToOwner = it[Likes.visibleToOwner]
                    )
                )
            }
        }

        _likeMessages = temps.map {
            val user = it.user
            val like = it.like
            val find = likedArticles.find { a -> a.id == like.articleId }
            LikeMessage(
                likeUser = user,
                article = find!!,
                timestamp = like.timestamp,
                likeId = like.id
            )
        }.sortedByDescending { it.timestamp }
    }
}


data class Temp(
    val user: User,
    val like: Like
)

@kotlinx.serialization.Serializable
data class LikeMessage(
    val likeId: Long,
    val likeUser: User, //点赞人
    val article: Article, //点赞的文章
    val timestamp: Long
)
