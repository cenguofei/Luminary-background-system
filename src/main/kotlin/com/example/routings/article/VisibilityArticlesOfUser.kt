package com.example.routings.article

import com.example.dao.article.VisibilityArticlesOfUserDao
import com.example.models.Article
import com.example.models.VisibleMode
import com.example.models.responses.PageOptions
import com.example.models.responses.pagesData
import com.example.models.tables.Articles
import com.example.plugins.security.noSession
import com.example.util.privacyArticlesOfUserPath
import com.example.util.publicArticlesOfUserPath
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Route.privacyArticlesOfUser() {
    authenticate {
        pagesData<Article>(
            requestPath = privacyArticlesOfUserPath,
            pageOptions = PageOptions(
                onCreateDao = {
                    val userId = it.parameters["userId"]?.toLong()!!
                    VisibilityArticlesOfUserDao(
                        userId = userId,
                        predicate = { Articles.visibleMode eq VisibleMode.OWN.name }
                    )
                },
                onIntercept = {
                    it.parameters["userId"]?.toLongOrNull() == null || it.noSession
                },
            )
        )
    }
}

fun Route.publicArticlesOfUser() {
    pagesData<Article>(
        requestPath = publicArticlesOfUserPath,
        pageOptions = PageOptions(
            onCreateDao = {
                val userId = it.parameters["userId"]?.toLong()!!
                VisibilityArticlesOfUserDao(
                    userId = userId,
                    predicate = { Articles.visibleMode eq VisibleMode.PUBLIC.name }
                )
            },
            onIntercept = {
                it.parameters["userId"]?.toLongOrNull() == null
            }
        )
    )
}