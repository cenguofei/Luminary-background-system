package com.example.dao.article.recommend

import com.example.dao.LunimaryPage
import com.example.models.Article

/**
 * ��¼�û������������
 *
 * �����Ƽ�ʱ��ֹ�ظ���������
 *
 * ���磺
 * �û��������20�����ݣ���ʱ��5�����ݵ����ȼ����ˣ��ܵ����Ƽ������б�ĵ�һ��λ�ã�
 * ����pageStart=3, perPageCount=5����ô����û�֮ǰ��������ʧ�ܣ���ʱִ��ˢ�²������ͻ᷵��pageStart=3, perPageCount=5�����ݣ�
 * �������������֮ǰ���ع��ģ������ݱ�5�����ȼ��ߵļ�������ȥ�ˣ������������Ƶ�������ᵼ���û���ȡ���ظ������ݣ�������Ҫ��¼�����������ID
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