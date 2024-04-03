package com.example.dao.article

import com.example.dao.friend.FriendDao
import com.example.models.Article
import com.example.models.VisibleMode
import com.example.models.tables.Articles
import com.example.models.tables.Friends
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
        if (allArticles.isNullOrEmpty()) {
            return emptyList()
        }
        return allArticles!!.page(pageStart, perPageCount)
    }

    override suspend fun pageCount(): Long {
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
                    (Friends.whoId eq loginUserId) and (Friends.userId inList myFollowing) and (Articles.visibleMode neq VisibleMode.OWN.name)
                }
            ).selectAll().mapToArticle()
        }.also {  list ->
            "friendsArticles:${list.map { it.id }}".logd("friends_test")
        }
    }
}
