package com.example.dao.article.recommend

import com.example.dao.collect.CollectDao
import com.example.dao.comment.CommentDao
import com.example.dao.like.LikeDao
import com.example.dao.view.ViewArticleDao
import com.example.models.ext.UserFriend
import com.example.models.tables.DELETED_ARTICLE_ID
import com.example.util.logi

interface UserActions {
    /**
     * 根据用户ID， 返回跟此用户关联的[UserAction]
     */
    suspend fun userActions(loginUserId: Long): List<UserAction>
}

fun UserActions(
    friends: List<UserFriend>
): UserActions = UserActionsImpl(friends)

internal class UserActionsImpl(
    private val friends: List<UserFriend>
) : UserActions {

    override suspend fun userActions(loginUserId: Long): List<UserAction> {
        val friendIds = friends.map { it.user.id }
        val result = mutableListOf<UserAction>()
        //1. ActionType.View
        val viewArticles = ViewArticleDao.friendsViewArticles(friendIds)
            .map {
                UserAction(
                    userId = it.userId,
                    articleId = it.articleId,
                    actionType = ActionType.VIEW
                )
            }
        result += viewArticles
        "viewArticles:${viewArticles}".logi("recommend_articles")

        //2. ActionType.Like
        val likeArticles = LikeDao.friendsLikeArticles(friendIds)
            .map {
                UserAction(
                    userId = it.userId,
                    articleId = it.articleId,
                    actionType = ActionType.LIKE
                )
            }
        result += likeArticles
        "likeArticles:${likeArticles}".logi("recommend_articles")

        //3. ActionType.Comment
        val friendComments = CommentDao.friendComments(friendIds)
            .map {
                UserAction(
                    userId = it.userId,
                    articleId = it.articleId,
                    actionType = ActionType.Comment
                )
            }
        result += friendComments
        "friendComments:${friendComments}".logi("recommend_articles")

        //4. ActionType.Collect
        val friendCollects = CollectDao.friendCollects(friendIds)
            .map {
                UserAction(
                    userId = it.collectUserId,
                    articleId = it.articleId,
                    actionType = ActionType.COLLECT
                )
            }
        result += friendCollects
        "friendCollects:${friendCollects}".logi("recommend_articles")

        return result.filter { it.articleId != DELETED_ARTICLE_ID }
    }
}