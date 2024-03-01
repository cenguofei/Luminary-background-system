package com.example

import com.example.models.printTestArticle
import com.example.plugins.*
import com.example.plugins.security.configureJWT
import com.example.plugins.security.configureSession
import com.example.routings.article.configureArticleRouting
import com.example.test.insertArticles
import com.example.util.logd
import com.example.util.periodicWork
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.tomcat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun main() {
    embeddedServer(Tomcat, port = 8080, host = "10.0.2.2", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSockets()
    configureSerialization()
    configureTemplating()
    configureMonitoring()
    configureSession()
    configureJWT()
    configureRouting()
    configureDoubleReceive()
    periodicWork()
    printTestArticle()
    test()
}

private fun test() {
    CoroutineScope(Job()).launch {
        insertArticles()
    }
}
