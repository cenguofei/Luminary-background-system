package com.example.routings.user.interactiondata

import com.example.dao.article.ArticleDao
import com.example.dao.article.LikeDao
import com.example.dao.friend.FriendDao
import com.example.models.ext.InteractionData
import com.example.routings.user.logic.MyFollowers
import com.example.routings.user.logic.MyFollowings

class InteractionImpl : Interaction {
    override suspend fun interactionData(loginUserId: Long): InteractionData {
        val myFollowers = MyFollowers.myFollowers(loginUserId)
        val myFollowings = MyFollowings.myFollowings(loginUserId)
        val mutualFollowUsers = FriendDao.mutualFollowUsers(loginUserId)
            .distinctBy { it.user.id }
        val articles = ArticleDao.getArticlesOfUser(loginUserId)
        val likesNum = LikeDao.likesNumOfUserArticles(loginUserId, articles.map { it.id })
        return InteractionData(
            likeNum = likesNum,
            friendNum = mutualFollowUsers.size.toLong(),
            followerNum = myFollowers.size.toLong(),
            followingNum = myFollowings.size.toLong()
        )
    }
}