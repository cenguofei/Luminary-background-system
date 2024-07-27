package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.dao.collect.ArticlesOfUserCollected
import com.example.dao.like.ArticlesOfUserLiked
import com.example.dao.user.UserDao
import com.example.models.Article
import com.example.models.responses.PageOptions
import com.example.models.responses.pagesData
import com.example.routings.article.view.viewDurationRoute
import com.example.routings.article.view.whenBrowseArticleRoute
import com.example.util.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureArticleRouting() {
    val articleDao = ArticleDao
    routing {
        route(articlesRootPath) {
            createArticleRoute(articleDao)
            getArticleById(articleDao)
            updateArticleRoute(articleDao)
            deleteArticleByIdRoute(articleDao)
            pageAllArticles(articleDao)
            publicArticlesOfUser()
            privacyArticlesOfUser()
            getAllArticlesOfUserCollected()
            getAllArticlesOfUserLiked()
            pageFriendsArticles()
            pageRecommendedArticles()
            whenBrowseArticleRoute(articleDao)
            viewDurationRoute()
            existingArticle()

            // approve
            approveArticleRoute()
        }
    }
}

/**
 * 用户点赞过的文章
 * 需要传递用户的id
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
 * 用户收藏的文章
 * 需要传递用户的id
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