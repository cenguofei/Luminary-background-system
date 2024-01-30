package com.example.models

import com.example.models.Users.autoIncrement
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.json

object Articles : Table() {
    val id = long("id").autoIncrement()

    val userId = long("user_id")
    val username = varchar("username", 50)
    val author = varchar("author", 20)
    val title = text("title")
    val link = text("link")
    val body = largeText("body")

    val niceDate = date("nice_date")

    val visibleMode = enumeration<VisibleMode>("visible_mode")

    val tags = json<Array<String>>("tags", Json.Default)

    val likes = json<Array<Like>>("likes", Json.Default)

    val collections = json<Array<Collect>>("collections", Json.Default)

    val comments = json<Array<Comment>>("comments", Json.Default)

    val viewsNum = integer("views_num")

    override val primaryKey = PrimaryKey(id)
}