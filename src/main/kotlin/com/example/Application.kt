package com.example

import com.example.models.printTestArticle
import com.example.plugins.*
import com.example.plugins.security.configureJWT
import com.example.plugins.security.configureSession
import com.example.routings.article.configureArticleRouting
import com.example.util.logd
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.tomcat.*

fun main() {
    embeddedServer(Tomcat, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    System.currentTimeMillis().toString().logd("timestamp")
    System.currentTimeMillis().toString().logd("timestamp")
    System.currentTimeMillis().toString().logd("timestamp")
    System.currentTimeMillis().toString().logd("timestamp")
    configureSockets()
    configureSerialization()
    configureTemplating()
    configureMonitoring()
    configureSession()
    configureJWT()
    configureRouting()
    configureDoubleReceive()
    printTestArticle()
}

