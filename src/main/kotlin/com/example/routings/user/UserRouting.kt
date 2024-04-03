package com.example.routings.user

import com.example.dao.user.UserDao
import com.example.models.Role
import com.example.models.User
import com.example.models.responses.PageOptions
import com.example.models.responses.pagesData
import com.example.plugins.security.jwtUser
import com.example.plugins.security.noSession
import com.example.routings.user.interactiondata.interactionDataRoute
import com.example.routings.user.routes.*
import com.example.util.pageUsersPath
import com.example.util.userRootPath
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureUserRouting() {
    routing {
        route(userRootPath) {
            val userDao = UserDao
            crud(userDao)
            checkIsLoginRoute()
            loginRoute(userDao)
            logoutRoute()
            registerRoute(userDao)
            pageUsers(userDao)
            interactionDataRoute()
        }
    }
}

private fun Route.pageUsers(userDao: UserDao) {
    authenticate {
        pagesData<User>(
            requestPath = pageUsersPath,
            pageOptions = PageOptions(
                onCreateDao = { userDao },
                onIntercept = {
                    it.noSession || it.jwtUser?.role != Role.Manager
                }
            )
        )
    }
}