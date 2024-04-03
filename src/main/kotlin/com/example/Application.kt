package com.example

import com.example.plugins.*
import com.example.plugins.security.configureJWT
import com.example.plugins.security.configureSession
import com.example.test.insertArticles
import com.example.util.periodicWork
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.tomcat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * TODO
 * 1. 加密密码
 * 2. 推荐算法
 * 3. 生成/抓取文章数据
 */
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
//    printTestArticle()
//    test()
}

private fun test() {
    CoroutineScope(Job()).launch {
        insertArticles()
    }
}
