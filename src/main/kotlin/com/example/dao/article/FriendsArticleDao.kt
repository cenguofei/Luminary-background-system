package com.example.dao.article

import com.example.dao.friend.FriendDao
import com.example.models.Article
import com.example.models.tables.Articles
import com.example.models.tables.Friends
import com.example.util.Default
import com.example.util.dbTransaction
import com.example.util.logd
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.selectAll

class FriendsArticleDao(private val loginUserId: Long) : DefaultArticleDao() {
    @Volatile
    private var allArticles: List<Article>? = null

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Article> {
        if (allArticles == null) {
            allArticles = getAllArticles()
        }
        val offset = pageOffset(pageStart, perPageCount).toInt()
        val fromIndex = offset + Int.Default
        var toIndex = offset + perPageCount
        val size = allArticles!!.size
        if (fromIndex >= size) {
            return emptyList()
        }
        if (toIndex > size) {
            toIndex = size
        }
        return allArticles!!.subList(
            fromIndex = fromIndex,
            toIndex = toIndex
        ).also { list ->
            "pages:${list.map { it.id }}".logd("friends_test")
            allArticles = null
        }
    }

    override suspend fun count(): Long {
        return if (allArticles == null) getAllArticles().also {
            allArticles = it
        }.size.toLong() else allArticles!!.size.toLong()
    }

    private suspend fun getAllArticles(): List<Article> {
        return dbTransaction {
            val myFollowing = FriendDao.myFollowings(loginUserId).map { it.whoId }
            Articles.innerJoin(
                otherTable = Friends,
                onColumn = { this.userId },
                otherColumn = { this.userId },
                additionalConstraint = {
                    (Friends.whoId eq loginUserId) and (Friends.userId inList myFollowing)
                }
            ).selectAll().mapToArticle()
        }.also {  list ->
            "friendsArticles:${list.map { it.id }}".logd("friends_test")
        }
    }
}
