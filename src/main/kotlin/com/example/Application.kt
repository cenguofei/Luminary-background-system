package com.example

import com.example.plugins.*
import com.example.plugins.security.configureJWT
import com.example.plugins.security.configureSession
import com.example.util.periodicWork
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.tomcat.*

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
    test()
}

private fun test() {
//    CoroutineScope(Job()).launch {
//        dbTransaction {
//            Articles.deleteWhere {
//                link like "%juejin.cn%"
//            }
//        }
//    }
}
