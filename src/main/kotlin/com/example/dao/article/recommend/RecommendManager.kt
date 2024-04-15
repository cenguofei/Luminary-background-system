package com.example.dao.article.recommend

import com.example.dao.LunimaryPage
import com.example.models.Article

/**
 * 记录用户浏览过的文章
 *
 * 智能推荐时防止重复推送文章
 *
 * 例如：
 * 用户浏览到第20条数据，此时有5条数据的优先级变了，跑到了推荐文章列表的第一个位置，
 * 假设pageStart=3, perPageCount=5，那么如果用户之前请求数据失败，此时执行刷新操作，就会返回pageStart=3, perPageCount=5的数据，
 * 但是这个数据是之前返回过的，旧数据被5条优先级高的挤到后面去了，还有其他类似的情况都会导致用户获取到重复的数据，所以需要记录浏览过的文章ID
 */
object RecommendManager {
    /**
     * key: loginUserId
     * value: articleIds
     */
    private val cache = mutableMapOf<Long, LunimaryPage<Article>>()

    fun getOrPut(
        key: Long,
        default: () -> LunimaryPage<Article>
    ): LunimaryPage<Article> {
        return cache[key] ?: run {
            val create = default()
            cache[key] = create
            create
        }
    }

    fun remove(key: Long) {
        if(cache.containsKey(key)) {
            cache.remove(key)
        }
    }
}

suspend fun <K, V> LRUCache<K, V>.getOrPut(key: K, default: suspend () -> V): V {
    val value = this[key]
    if (value != null) return value

    val create = default()
    this[key] = create
    return create
}