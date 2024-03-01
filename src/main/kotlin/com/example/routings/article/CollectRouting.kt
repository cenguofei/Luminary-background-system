package com.example.routings.article

import com.example.dao.article.CollectDao
import com.example.dao.article.CollectDaoImpl
import com.example.models.Collect
import com.example.models.responses.DataResponse
import com.example.models.responses.pagesData
import com.example.plugins.receive
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureCollectRouting() {
    val collectDao: CollectDao = CollectDaoImpl()
    routing {
        route(collectRootPath) {
            createCollect(collectDao)
            deleteCollect(collectDao)
            getAllCollectsOfArticle(collectDao)
            getAllCollectsOfUser(collectDao)
            pageCollects(collectDao)
        }
    }
}

private fun Route.pageCollects(collectDao: CollectDao) {
    pagesData(
        dao = collectDao,
        requestPath = pageCollectsPath
    )
}

private fun Route.createCollect(collectDao: CollectDao) {
    authenticate {
        post(createCollectPath) {
            if (call.noSession()) {
                return@post
            }
            call.receive<Collect> {
                if (it.collectUserId != call.jwtUser?.id) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = DataResponse<Unit>().copy(msg = internalErrorMsg)
                    )
                    return@post
                }
                collectDao.create(it)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = DataResponse<Unit>()
                )
            }
        }
    }
}

/**
 * 传递需要删除的Collect id
 */
private fun Route.deleteCollect(collectDao: CollectDao) {
    authenticate {
        delete(deleteCollectPath) {
            if (call.invalidId()) {
                return@delete
            }
            collectDao.delete(call.id)
            call.respond(
                status = HttpStatusCode.OK,
                message = DataResponse<Unit>().copy(msg = deleteSuccess)
            )
        }
    }
}


/**
 * 文章获得的所有点赞
 * 需要传递文章id
 */
private fun Route.getAllCollectsOfArticle(collectDao: CollectDao) {
    get(getAllCollectsOfArticlePath) {
        if (call.invalidId()) {
            return@get
        }
        val likes = collectDao.getAllCollectsOfArticle(call.id)
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<List<Collect>>().copy(data = likes)
        )
    }
}

/**
 * 用户的所有点赞
 * 需要传递用户的id
 */
private fun Route.getAllCollectsOfUser(collectDao: CollectDao) {
    get(getAllCollectsOfUserPath) {
        if (call.invalidId()) {
            return@get
        }
        val likes = collectDao.getAllCollectsOfUser(call.id)
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<List<Collect>>().copy(data = likes)
        )
    }
}
