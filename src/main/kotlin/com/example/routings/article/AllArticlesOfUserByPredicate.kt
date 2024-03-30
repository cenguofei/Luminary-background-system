package com.example.routings.article

import com.example.dao.article.ArticlesOfUserCollected
import com.example.dao.article.ArticlesOfUserLiked
import com.example.dao.user.UserDao
import com.example.models.Article
import com.example.models.responses.PageOptions
import com.example.models.responses.pagesData
import com.example.util.getAllArticlesOfUserCollectedPath
import com.example.util.getAllArticlesOfUserLikedPath
import com.example.util.id
import com.example.util.invalidIdNoRespond
import io.ktor.server.routing.*

/**
 * �û����޹�������
 * ��Ҫ�����û���id
 */
fun Route.getAllArticlesOfUserLiked() {
    pagesData<Article>(
        requestPath = getAllArticlesOfUserLikedPath,
        pageOptions = PageOptions(
            onIntercept = { it.invalidIdNoRespond("userId") || !UserDao.existing(it.id("userId")) },
            onCreateDao = { ArticlesOfUserLiked(it.id("userId")) }
        )
    )
}

/**
 * �û��ղص�����
 * ��Ҫ�����û���id
 */
fun Route.getAllArticlesOfUserCollected() {
    pagesData<Article>(
        requestPath = getAllArticlesOfUserCollectedPath,
        pageOptions = PageOptions(
            onIntercept = {
                it.invalidIdNoRespond("userId") || !UserDao.existing(it.id("userId"))
            },
            onCreateDao = {
                ArticlesOfUserCollected(it.id("userId"))
            }
        )
    )
}