package com.example.routings.article.collect

import com.example.dao.collect.CollectDao
import com.example.models.Collect
import com.example.models.responses.DataResponse
import com.example.models.responses.PageOptions
import com.example.models.responses.pagesData
import com.example.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureCollectRouting() {
    val collectDao = CollectDao
    routing {
        route(collectRootPath) {
            createCollectRoute(collectDao)
            deleteCollect(collectDao)
            getAllCollectsOfArticle(collectDao)
            pageCollects(collectDao)
            existsCollectRoute(collectDao)
            cancelCollectRoute(collectDao)
        }
    }
}

private fun Route.pageCollects(collectDao: CollectDao) {
    pagesData<Collect>(
        pageOptions = PageOptions { collectDao },
        requestPath = pageCollectsPath
    )
}



/**
 * 传递需要删除的Collect id
 */
private fun Route.deleteCollect(collectDao: CollectDao) {
    authenticate {
        delete(deleteCollectPath) {
            if (call.invalidId<Unit>()) {
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
//    get(getAllCollectsOfArticlePath) {
//        if (call.invalidId<List<Article>>()) {
//            return@get
//        }
//        val likes = collectDao.getAllCollectsOfArticle(call.id)
//        call.respond(
//            status = HttpStatusCode.OK,
//            message = DataResponse<List<Article>>().copy(data = likes)
//        )
//    }
}

