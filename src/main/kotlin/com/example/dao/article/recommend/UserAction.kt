package com.example.dao.article.recommend

/**
 * �û������Ϊ����ģ��
 */
data class UserAction(
    val userId: Long,
    val articleId: Long,
    val actionType: ActionType
)