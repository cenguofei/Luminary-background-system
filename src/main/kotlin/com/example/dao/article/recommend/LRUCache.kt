package com.example.dao.article.recommend


open class LRUCache<K, V>(private val capacity: Int = 128) :
    LinkedHashMap<K, V>(capacity, 0.75f, true) {

    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return size > capacity
    }
}
