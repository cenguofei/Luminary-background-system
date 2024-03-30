package com.example.models.responses

import com.example.dao.LunimaryPage
import io.ktor.server.application.*
import io.ktor.util.pipeline.*

interface PageOptions {
    suspend fun interceptor(call: ApplicationCall): Boolean

    fun <T> createDao(call: ApplicationCall): LunimaryPage<T>
}

fun <T> PageOptions(
    onIntercept: suspend (ApplicationCall) -> Boolean = { false },
    onCreateDao: (ApplicationCall) -> LunimaryPage<T>
): PageOptions = object : PageOptions {
    override suspend fun interceptor(call: ApplicationCall): Boolean {
        return onIntercept(call)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> createDao(call: ApplicationCall): LunimaryPage<T> {
        val dao = onCreateDao(call)
        return dao as LunimaryPage<T>
    }
}