package com.example.dao.article.recommend

import com.example.dao.friend.FriendDao
import com.example.models.Article
import com.example.models.ext.UserFriend
import com.example.util.logi

/**
 * �Ƽ��㷨��
 * @property articles �����ݿ��ж�ȡ�������б��д�������ʱΪ�������£����ݿ�������������СʱΪȫ�����£�Ŀǰ������������̫�Ӵ�����Ĭ��Ϊ���ݿ��е�ȫ������
 */
class RecommendationByFriendEngineImpl(
    private val articles: List<Article>
) : RecommendationByFriendEngine {
    private val defaultAcc = 0

    override suspend fun recommendArticles(loginUserId: Long): List<Article> {
        // ��ȡ�û��������б�
        val friends = FriendDao.mutualFollowUsers(loginUserId)
        "friends=$friends".logi(RECOMMEND_TAG)
        // �����û����ѵ���������ޡ����ۺ��ղ���Ϊ�����Ƽ�
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

        // �����Ƽ�Ȩ�����������б�
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

    // ��ȡ�û�����Ϊ�б�
    private suspend fun getUserActions(userId: Long, friends: List<UserFriend>): List<UserAction> {
        if (_userActions == null) {
            _userActions = UserActions(friends).userActions(userId)
        }
        return userActions.filter { it.userId == userId }
    }
}