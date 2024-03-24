package com.example.models.responses

import com.example.dao.LuminaryDao
import com.example.util.Default
import com.example.util.empty
import com.example.util.logd
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

/**
 * request:
 * http://localhost:8080/article/all?curPage=0&pageCount=12
 */
@Serializable
class PageResponse<T> : BaseResponse<Page<T>>() {
    fun copy(
        msg: String = empty,
        data: Page<T>? = null
    ): PageResponse<T> {
        this.msg = msg
        this.data = data
        return this
    }
}

@Serializable
data class Page<T>(
    /**
     * prevPage = curPage - 1
     * nextPage = curPage + 1
     */
    val curPage: Int = 0,

    /**
     * 总页数
     */
    val pageSize: Long = 0,

    /**
     * 数据库中文章的数量
     */
    val totalArticle: Long = 0,

    val lists: List<T> = emptyList()
)

fun calculatePageSize(count: Long, perPageCount: Int): Long {
    return if (count % perPageCount != 0L) count / perPageCount + 1 else count / perPageCount
}

/**
 * 分页获取数据
 * curPage: 从0开始
 * @param onCall 返回值代表是否终止后续步骤
 */
inline fun <reified D, P : Table> Route.pagesData(
    requestPath: String,
    defaultCurPage: Int = Int.Default,
    defaultPageCount: Int = 24,
    crossinline createDao: (ApplicationCall) -> LuminaryDao<D, P>,
    crossinline onCall: (ApplicationCall) -> Boolean = { false }
) {
    get(requestPath) {
        if (onCall(call)) {
            call.respond(
                status = HttpStatusCode.Conflict,
                message = PageResponse<D>()
            )
            return@get
        }
        val queryParameters = call.request.queryParameters
        val wishPage = queryParameters["wishPage"]?.toInt() ?: defaultCurPage
        val perPageCount = queryParameters["perPageCount"]?.toInt() ?: defaultPageCount
        val dao = createDao(call)
        val count = dao.count()
        val lists = dao.pages(wishPage, perPageCount = perPageCount)
        call.respond(
            status = HttpStatusCode.OK,
            message = PageResponse<D>().copy(
                data = Page(
                    curPage = wishPage,
                    pageSize = calculatePageSize(count, perPageCount),
                    totalArticle = count,
                    lists = lists
                )
            )
        )
        "查询到的数据总量：count=$count, wishPage=$wishPage, perPageCount=$perPageCount".logd("pages_d")
    }
}