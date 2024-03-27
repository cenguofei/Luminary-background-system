package com.example.routings.search

import com.example.util.searchRootPath
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureSearchRouting() {
    routing {
        route(searchRootPath) {
            searchArticle()
            searchUser()
        }
    }
}