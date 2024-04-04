package com.example.models.responses

import com.example.util.Default
import com.example.util.empty
import com.example.util.logd
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun calculatePageSize(count: Long, perPageCount: Int): Long {
    return if (count % perPageCount != 0L) count / perPageCount + 1 else count / perPageCount
}


/**
 * 分页获取数据
 * curPage: 从0开始
 */
inline fun <reified D> Route.pagesData(
    requestPath: String,
    defaultCurPage: Int = Int.Default,
    defaultPageCount: Int = 24,
    pageOptions: PageOptions
) {
    get(requestPath) {
        if (pageOptions.interceptor(call)) {
            call.respond(
                status = HttpStatusCode.Conflict,
                message = PageResponse<D>().copy(
                    msg = "Intercepted !"
                )
            )
            return@get
        }
        val queryParameters = call.request.queryParameters
        val wishPage = queryParameters["wishPage"]?.toInt() ?: defaultCurPage
        val perPageCount = queryParameters["perPageCount"]?.toInt() ?: defaultPageCount
        val dao = pageOptions.createDao<D>(call)
        val lists = dao.pages(pageStart = wishPage, perPageCount = perPageCount)
        val count = dao.pageCount()
        call.respond(
            status = HttpStatusCode.OK,
            message = PageResponse<D>().copy(
                data = Page(
                    curPage = wishPage,
                    pageSize = calculatePageSize(count, perPageCount),
                    total = count,
                    lists = lists,
                ),
                msg = if (wishPage == 0 && lists.isEmpty()) {
                    "No relevant data found."
                } else empty
            )
        )
        "count=$count, wishPage=$wishPage, perPageCount=$perPageCount".logd("pages_d")
        "page detail: $lists".logd("pages_d")
    }
}

val ApplicationCall.wishPage: Int get() = request.queryParameters["wishPage"]?.toInt() ?: 0

val ApplicationCall.perPageCount: Int get() = request.queryParameters["perPageCount"]?.toInt() ?: 24