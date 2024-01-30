package com.example.models

import com.example.util.Default
import com.example.util.empty
import com.example.util.logd
import com.google.gson.Gson
import kotlinx.serialization.json.Json
import java.time.format.DateTimeFormatter

@kotlinx.serialization.Serializable
data class Article(
    val id: Long = Long.Default,

    val userId: Long = Long.Default,

    val username: String = empty,

    val author: String = empty,

    val title: String = empty,

    val link: String = empty, // content

    val body: String = empty, // content

    /**
     * format of publishTime
     */
    val niceDate: String = empty,

    /**
     * ���¿ɼ���Χ
     */
    val visibleMode: VisibleMode = VisibleMode.PUBLIC,

    val tags: Array<String> = emptyArray(),

    val likes: Array<Like> = emptyArray(),

    val collections: Array<Collect> = emptyArray(),

    val comments: Array<Comment> = emptyArray(),

    /**
     * �������
     */
    val viewsNum: Int = Int.Default
) {
    /**
     * ���·����˶�����
     */
    val daysFromToday: Int get() {
        return Int.Default
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Article

        if (userId != other.userId) return false
        if (username != other.username) return false
        if (author != other.author) return false
        if (title != other.title) return false
        if (link != other.link) return false
        if (body != other.body) return false
        if (niceDate != other.niceDate) return false
        if (visibleMode != other.visibleMode) return false
        if (!tags.contentEquals(other.tags)) return false
        if (!likes.contentEquals(other.likes)) return false
        if (!collections.contentEquals(other.collections)) return false
        if (!comments.contentEquals(other.comments)) return false
        if (viewsNum != other.viewsNum) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + link.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + niceDate.hashCode()
        result = 31 * result + visibleMode.hashCode()
        result = 31 * result + tags.contentHashCode()
        result = 31 * result + likes.contentHashCode()
        result = 31 * result + collections.contentHashCode()
        result = 31 * result + comments.contentHashCode()
        result = 31 * result + viewsNum
        return result
    }
}


enum class VisibleMode {
    OWN,
    PUBLIC,
    FRIEND
}

val testArticle = Article(
    userId = 10007,
    username = "ttt",
    author = "Chen Guofei",
    title = "Self study KTor development LuminaryBlog backend",
    body = "Note: Databases that support a path context root will have this value appended to the generated SQL path expression by default, so it is not necessary to include it in the provided argument String. In the above example, if MySQL is being used, the provided path arguments should be .name and .language respectively.",
    niceDate = "2019-11-15",
    visibleMode = VisibleMode.PUBLIC,
    tags = arrayOf("Kotlin", "Compose", "Android", "Ktor"),
    collections = arrayOf(Collect(10004), Collect(10008)),
    comments = arrayOf(Comment(comment = "You old six"), Comment("Zhang Wansen, it's snowing")),
    viewsNum = 99
)

fun printTestArticle() {
    val json = Gson().toJson(testArticle)
    "testArticle=$json".logd("article")
}