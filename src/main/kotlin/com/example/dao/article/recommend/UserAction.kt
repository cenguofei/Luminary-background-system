package com.example.dao.article.recommend

/**
 * 用户浏览行为数据模型
 */
data class UserAction(
    val userId: Long,
    val articleId: Long,
    val actionType: ActionType
)