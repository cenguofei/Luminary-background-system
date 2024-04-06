package com.example.routings.wanandroid

import com.example.dao.article.ArticleDao
import com.example.models.responses.DataResponse
import com.example.plugins.receive
import com.example.util.logd
import com.example.util.wanandroidSaveArticlesPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureWanandroidRouting() {
    routing {
        authenticate {
            post(wanandroidSaveArticlesPath) {
                call.receive<WanandroidArticles> {
                    val articles = it.articles
                    if (articles.isEmpty()) {
                        return@post
                    }
                    val ids = ArticleDao.insertBatch(articles)
                    "articles:$ids, was saved".logd("wanandroid_save")
                }
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>()
                )
            }
        }
    }
}