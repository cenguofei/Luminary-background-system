package com.example.dao.article.recommend

/**
 * 用户行为类型枚举
 * @property weight 权重
 */
enum class ActionType(val weight: Int) {
    /**
     * 朋友浏览过的文章
     */
    VIEW(1),

    /**
     * 朋友点赞过的文章
     */
    LIKE(2),

    /**
     * 朋友评论过的文章
     */
    Comment(3),

    /**
     * 朋友收藏的文章
     */
    COLLECT(4)
}
