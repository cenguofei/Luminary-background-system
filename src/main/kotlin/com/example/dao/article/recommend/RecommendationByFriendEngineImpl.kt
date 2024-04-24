package com.example.dao.article.recommend

import com.example.dao.friend.FriendDao
import com.example.models.Article
import com.example.models.ext.UserFriend
import com.example.util.logi

/**
 * 推荐算法类
 * @property articles 从数据库中读取的文章列表，有大量文章时为部分文章，数据空中文章数量较小时为全部文章，目前文章数量不会太庞大，所以默认为数据库中的全部文章
 */
class RecommendationByFriendEngineImpl(
    private val articles: List<Article>
) : RecommendationByFriendEngine {
    private val defaultAcc = 0

    override suspend fun recommendArticles(loginUserId: Long): List<Article> {
        // 获取用户的朋友列表
        val friends = FriendDao.mutualFollowUsers(loginUserId)
        "friends=$friends".logi(RECOMMEND_TAG)
        // 根据用户朋友的浏览、点赞、评论和收藏行为进行推荐
        val recommendedArticles = mutableMapOf<Long, Int>()
        for (friend in friends) {
            val friendActions = getUserActions(friend.user.id, friends)
            for (action in friendActions) {
                val weight = action.actionType.weight
                recommendedArticles[action.articleId] =
                    recommendedArticles.getOrDefault(action.articleId, defaultAcc) + weight
            }
        }
        "recommendedArticles=$recommendedArticles".logi(RECOMMEND_TAG)

        // 根据推荐权重排序文章列表
        val sortedRecommendations = recommendedArticles.entries.sortedByDescending { it.value }
        "sortedRecommendations=$sortedRecommendations".logi(RECOMMEND_TAG)

        val recommendedArticleIds = sortedRecommendations.map { it.key }
        "recommendedArticleIds=$recommendedArticleIds".logi(RECOMMEND_TAG)

        return recommendedArticleIds.mapNotNull {
            articles.find { article -> article.id == it }
        }.also { list ->
            "fetchNew recommendedArticles=${list.map { it.id }}".logi(RECOMMEND_TAG)
        }
    }

    private var _userActions: List<UserAction>? = null
    private val userActions get() = _userActions!!

    // 获取用户的行为列表
    private suspend fun getUserActions(userId: Long, friends: List<UserFriend>): List<UserAction> {
        if (_userActions == null) {
            _userActions = UserActions(friends).userActions(userId)
        }
        return userActions.filter { it.userId == userId }
    }
}