package com.example.routings.article

import com.example.dao.article.ArticleDao
import com.example.dao.user.UserDao
import com.example.models.Article
import com.example.models.VisibleMode
import com.example.models.responses.DataResponse
import com.example.util.privacyArticlesOfUserPath
import com.example.util.publicArticlesOfUserPath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.privacyArticlesOfUser(articleDao: ArticleDao) {
    visibilityArticlesOfUser(
        path = privacyArticlesOfUserPath,
        articleDao = articleDao,
        predicate = { it.visibleMode == VisibleMode.OWN }
    )
}

fun Route.publicArticlesOfUser(articleDao: ArticleDao) {
    visibilityArticlesOfUser(
        path = publicArticlesOfUserPath,
        articleDao = articleDao,
        predicate = { it.visibleMode == VisibleMode.PUBLIC }
    )
}

private fun Route.visibilityArticlesOfUser(
    path: String,
    articleDao: ArticleDao,
    predicate: (Article) -> Boolean
) {
    get(path) {
        val userId = call.parameters["userId"]?.toLongOrNull()
        if (userId == null) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = DataResponse<List<Article>>().copy(
                    msg = "用户id为空",
                )
            )
            return@get
        }

        val userDao: UserDao = UserDao.Default
        val user = userDao.read(userId)
        if (user == null) {
            call.respond(
                status = HttpStatusCode.NotFound,
                message = DataResponse<List<Article>>().copy(
                    msg = "没有id为${userId}的用户"
                )
            )
            return@get
        }
        val articles = articleDao.getArticlesOfUser(userId).filter(predicate)
        call.respond(
            status = HttpStatusCode.OK,
            message = DataResponse<List<Article>>().copy(
                data = articles
            )
        )
    }
}