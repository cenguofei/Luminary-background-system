package com.example.routings.topic

import com.example.dao.article.ArticleDao
import com.example.dao.topic.TopicDao
import com.example.dao.topic.mapToTopic
import com.example.models.Topic
import com.example.models.responses.calculatePageSize
import com.example.models.tables.Topics
import com.example.util.dbTransaction
import com.example.util.logd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

val coroutine = CoroutineScope(Job())

const val DEFAULT_PER_PAGE_COUNT = 128

fun updateTopicRank() {
    coroutine.launch {
        update()
    }
}

private suspend fun update() {
    TopicDao
    val pageSize = calculatePageSize(ArticleDao.count(), DEFAULT_PER_PAGE_COUNT)
    var curPage = 0
    "pageSize=$pageSize".logd("update_topics")

    val topicMap = mutableMapOf<String, Int>()
    while (curPage < pageSize) {
        val tags = ArticleDao.pages(curPage, DEFAULT_PER_PAGE_COUNT)
            .filter { it.tags.isNotEmpty() }
            .flatMap { it.tags.toList() }

        tags.forEach {
            topicMap[it] = topicMap.getOrDefault(it, 0) + 1
        }
        "curPage=$curPage, tags=$tags".logd("update_topics")
        curPage++
    }
    val sortedTopics = topicMap.entries.sortedByDescending {
        it.value
    }.map {
        Topic(
            topic = it.key,
            fashion = it.value,
            timestamp = System.currentTimeMillis()
        )
    }
    sortedTopics.map {
        "{${it.topic}:${it.fashion}}"
    }.let { "sortedTopics=$it".logd("update_topics") }
    val size = dbTransaction {
        val dbTopics = Topics.selectAll().mapToTopic()
        val dbOnlyTopics = dbTopics.map { it.topic }
        val notExistTopics = sortedTopics.filter { it.topic !in dbOnlyTopics }
        val existTopics = sortedTopics.filter { it.topic in dbOnlyTopics }

        existTopics.forEach { topic ->
            Topics.update(
                where = { Topics.topic eq topic.topic },
                limit = 1
            ) { state ->
                state[fashion] = topic.fashion
            }
        }

        Topics.batchInsert(
            data = notExistTopics,
            ignore = true,
            shouldReturnGeneratedValues = false,
        ) {
            this[Topics.topic] = it.topic
            this[Topics.fashion] = it.fashion
            this[Topics.timestamp] = it.timestamp
        }
    }.size
    "insert size=$size".logd("update_topics")
}